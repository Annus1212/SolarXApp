package com.example.solarx.pages.viewM

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class loginViewModel: ViewModel() {

    fun isValidPassword(IP: String, Serial: String, callback: (Result<Boolean>) -> Unit): Unit {
        val client = OkHttpClient.Builder().connectTimeout(1500, TimeUnit.MILLISECONDS).build()

        // Define the URL for the POST request
        val url = "http://${IP}/"

        // Create a JSON media type
        // Define the raw JSON request body
        val formBody = FormBody.Builder()
            .add("optType", "newParaSetting")
            .add("subOption", "pwd")
            .add("Value", Serial)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        // Create a RequestBody instance
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
//                client.newCall(request).execute()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onResponse(call: Call, response: Response) {
                            val re = response.body?.string()?.get(0) == 'Y'
                            callback(Result.success(re))
                        }

                        override fun onFailure(call: Call, e: IOException) {
                            callback(Result.failure(e))
                        }
                    })
                }
            }
            catch (e: Exception)
            {
                callback(Result.failure(e))
            }
        }

    }
}