package com.brdx.movpelis.core

import com.brdx.movpelis.domain.WebService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY);
    private val client = OkHttpClient().newBuilder().addInterceptor(logging).build();

    val webservice: WebService by lazy {
        Retrofit
            .Builder()
            .baseUrl(WebService.base_url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }
}