package com.example.solarx.pages.viewM

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
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
        val client = HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 1500
            }
        }

        val url = "http://${IP}/"

        viewModelScope.launch {
            try {
                val response: HttpResponse = client.submitForm(
                    url = url,
                    formParameters = parameters {
                        append("optType", "newParaSetting")
                        append("subOption", "pwd")
                        append("Value", Serial)
                    }
                )
                val responseBody = response.bodyAsText()
                val isValid = responseBody.isNotEmpty() && responseBody[0] == 'Y'
                callback(Result.success(isValid))
            } catch (e: Exception) {
                callback(Result.failure(e))
            } finally {
                client.close()
            }
        }
    }
}