package com.example.cf.tutorialsondemand.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoogleLoginQuery {
    @FormUrlEncoded
    @POST("/register-by-token/google-plus/")
    fun loginGoogle(@Field("access_token") googleIdToken: String): Call<Any>
}