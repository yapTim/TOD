package com.example.cf.tutorialsondemand.retrofit

import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.models.Question
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by CF on 2/28/2018.
 */
interface ServerQuery {

    @GET("/livestream/?format=json")
    fun getOpentokIds(): Call<Opentok>

    // Pass category instead
    @FormUrlEncoded
    @POST("//")
    fun sendCategoryForFinding(@Field("id") category: Int): Call<Boolean>

    @FormUrlEncoded
    @POST("/trial/")
    fun sendToDummy(@Field("dummy") dummy: String): Call<String>

}