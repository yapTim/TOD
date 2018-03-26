package com.example.cf.tutorialsondemand.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Connect(url: String) {
    private val build = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = build.client(OkHttpClient.Builder().build()).build()

    val connection = retrofit.create(ServerQuery::class.java)
    val connectionFacebook = retrofit.create(FacebookLoginQuery::class.java)
    val connectionCategory = retrofit.create((CategoryQuery::class.java))
}