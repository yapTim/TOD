package com.example.cf.tutorialsondemand.retrofit

import retrofit2.Call
import retrofit2.http.*


interface FacebookLoginQuery {
    @FormUrlEncoded
    @POST("/register-by-token/facebook/")
    fun loginFacebook(@Field("access_token") fbApiAuthToken: String): Call<Long>
}