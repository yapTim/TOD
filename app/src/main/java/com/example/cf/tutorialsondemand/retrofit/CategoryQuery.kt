package com.example.cf.tutorialsondemand.retrofit

import retrofit2.Call
import retrofit2.http.GET
import com.example.cf.tutorialsondemand.models.QuestionCategory
import com.example.cf.tutorialsondemand.models.TutorCategory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by CF on 3/26/2018.
 */
interface CategoryQuery {

    @GET("/category")
    fun getCategory(): Call<List<QuestionCategory>>

    @FormUrlEncoded
    @POST("/tutor/")
    fun sendTutorCategory(@Field("user") userId: Int,
                          @Field("category") categoryList: IntArray,
                          @Field("status") isAvailable: Int): Call<TutorCategory>
}