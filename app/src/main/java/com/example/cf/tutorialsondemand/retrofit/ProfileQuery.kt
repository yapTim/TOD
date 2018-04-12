package com.example.cf.tutorialsondemand.retrofit

import com.example.cf.tutorialsondemand.models.User
import retrofit2.Call
import retrofit2.http.*

interface ProfileQuery {

    @FormUrlEncoded
    @POST("/rating/")
    fun rateTutor(
            @Field("user") tutorId: Long,
            @Field("rating") rating: Float
    ): Call<Boolean>

    @GET("/rating/")
    fun getNewRating(
            @Query("user") userId: Long
    ): Call<Double>

    @GET("/users/")
    fun getSearchResult(
            @Query("q") name: String
    ): Call<List<User>>
}