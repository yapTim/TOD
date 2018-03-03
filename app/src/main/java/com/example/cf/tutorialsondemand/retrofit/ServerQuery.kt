package com.example.cf.tutorialsondemand.retrofit

import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.models.Question
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by CF on 2/28/2018.
 */
interface ServerQuery {
    @GET("/question")
    fun getQuestions(): Call<List<Question>>

    @GET("/livestream")
    fun getOpentokIds(): Call<Opentok>
}