package com.example.solarx.pages.viewM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class loginViewModel: ViewModel() {

    fun isValidPassword(callback: (Boolean) -> Unit) {
        callback(true)

        val client = OkHttpClient()

        // Define the URL for the POST request
        val url = "http://192.168.100.13/"

        // Create a JSON media type
        // Define the raw JSON request body
        val formBody = FormBody.Builder()
            .add("optType", "newParaSetting")
            .add("subOption", "pwd")
            .add("Value", "SSFRXPLCGL")
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        // Create a RequestBody instance
        viewModelScope.launch {
            val response: Response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            if (!response.isSuccessful)
                throw IOException("Unexpected code $response")
            callback(response.body?.string()?.get(0) == 'Y')
        }

    }
}