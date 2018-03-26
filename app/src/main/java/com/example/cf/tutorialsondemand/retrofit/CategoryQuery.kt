package com.example.cf.tutorialsondemand.retrofit

import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by CF on 3/26/2018.
 */
interface CategoryQuery {

    @GET("auth/token")
    fun getCategory(): Call<List<String>>
}