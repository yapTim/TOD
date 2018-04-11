package com.example.cf.tutorialsondemand.retrofit

import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.*
import retrofit2.Call
import com.google.gson.annotations.SerializedName
import retrofit2.http.*

/**
 * Created by CF on 3/26/2018.
 */
interface CategoryQuery {

    @GET("/category/")
    fun getCategory(): Call<List<QuestionCategory>>

    @GET("/request-pool/retrieve-student/")
    fun sendTutorCategory(
            @Query("category") categoryList: IntArray
    ): Call<Student>

    @FormUrlEncoded
    @POST("/request-pool/")
    fun sendStudentCategory(
            @Field("user") userId: Long,
            @Field("category") categoryList: Int
    ): Call<RequestPoolObject>

    @GET("/request-pool/{poolId}/status-to-inactive/")
    fun setStudentToInactive(
            @Path("poolId") poolId: Long
    ): Call<Boolean>

    @GET("/room/{roomId}/status-to-inactive/")
    fun setRoomToInactive(
            @Path("roomId") roomId: Long
    ): Call<Boolean>
}