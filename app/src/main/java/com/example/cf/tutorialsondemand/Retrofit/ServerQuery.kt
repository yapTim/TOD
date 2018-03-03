package com.example.cf.tutorialsondemand.Retrofit

import com.example.cf.tutorialsondemand.Objects.Opentok
import com.example.cf.tutorialsondemand.Objects.Question
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by CF on 2/28/2018.
 */
interface ServerQuery {
    @GET("/question/?format=json")
    fun getQuestions(): Call<List<Question>>

    @GET("/livestream/?format=json")
    fun getOpentokIds(): Call<Opentok>
}