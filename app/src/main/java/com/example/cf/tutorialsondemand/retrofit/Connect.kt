package com.example.cf.tutorialsondemand.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Connect(url: String) {
    private val build = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = build.client(
            OkHttpClient
                    .Builder()
                    .build()).build()

    val connection = retrofit.create(ServerQuery::class.java)
    val connectionFacebook = retrofit.create(FacebookLoginQuery::class.java)
    val connectionCategory = retrofit.create((CategoryQuery::class.java))
    val connectionLivestream = retrofit.create(OpentokQuery::class.java)
    val connectionProfile = retrofit.create(ProfileQuery::class.java)
}