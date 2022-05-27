package com.tw.longerrelationship.logic.network

import com.tw.longerrelationship.util.logV
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val BASE_URL = "https://api.seniverse.com/v3/weather/"

    const val weatherApiSecreteKey = "SI3QcOEUfSKmdZqVt"

    var httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(LoggingInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)


    class LoggingInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val t1 = System.nanoTime()
            logV(TAG, "Sending request: ${request.url()} \n ${request.headers()}")

            val response = chain.proceed(request)
            val t2 = System.nanoTime()
            logV(
                TAG,
                "Received response for  ${
                    response.request().url()
                } in ${(t2 - t1) / 1e6} ms\n${response.headers()}"
            )
            return response
        }

        companion object {
            const val TAG = "LoggingInterceptor"
        }

    }

}