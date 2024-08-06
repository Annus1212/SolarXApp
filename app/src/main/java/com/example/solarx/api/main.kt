package com.example.solarx.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.cio.Response
import io.ktor.util.KtorExperimentalAPI
import okhttp3.Call
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

suspend fun isValidPassword() {
    val client = HttpClient(OkHttp) {
        engine {
            addInterceptor() { chain ->
                val originalResponse = chain.proceed(chain.request())
                Log.d("Response", originalResponse.headers.get("Content-Type").toString())
                val newHeaders = originalResponse.headers.newBuilder()
                    .add("X-Custom-Header", "CustomValue")
                    .build()

                // Create a new response with the modified headers
                originalResponse.newBuilder()
                    .headers(newHeaders)
                    .build()
            }
            }
        }
    
    val response: HttpResponse = client.post("http://192.168.100.13/"){
        formData {
            append("optType","newParaSetting")
            append("subOption", "pwd")
            append("Value", "SSFRXPLCGL")
        }
    }
    val contentType = response.headers["Content-Type"]
    Log.d("Content", contentType.toString())
}

fun isValidPassword2(): Boolean {
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
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.body!!.string()[0] == 'Y'
    }
}