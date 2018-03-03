package com.example.cf.tutorialsondemand.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Connect(url: String) {
    private val http = OkHttpClient.Builder()

    private val build = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = build.client(
            http.build()
    )
            .build()

    val connection = retrofit.create(ServerQuery::class.java)

}