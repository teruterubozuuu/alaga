package com.example.medicalapp.network

import com.example.alaga.BuildConfig
import com.example.alaga.OpenAiApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenAiClient {

    private const val BASE_URL = "https://api.openai.com/"

    private val apiKey: String = BuildConfig.OPENAI_API_KEY

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                .build()
            chain.proceed(request)
        })
        .build()

    val api: OpenAiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiApi::class.java)
    }
}
