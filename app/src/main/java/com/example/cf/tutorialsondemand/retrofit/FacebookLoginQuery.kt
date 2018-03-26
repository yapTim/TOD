package com.example.cf.tutorialsondemand.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface FacebookLoginQuery {

    @POST("auth/token")
    fun loginFacebook(@Body fbApiAuthToken: String): Call<Boolean>
}