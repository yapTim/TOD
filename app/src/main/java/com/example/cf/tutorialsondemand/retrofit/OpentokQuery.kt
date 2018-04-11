package com.example.cf.tutorialsondemand.retrofit

import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.models.Tutor
import retrofit2.Call
import retrofit2.http.*

interface OpentokQuery {

    @FormUrlEncoded
    @POST("/room/")
    fun makeRoom(
            @Field("category") category: Int,
            @Field("user_student") studentId: Long,
            @Field("user_tutor") tutorId: Long
    ): Call<Long>

    @GET("/room/retrieve-room/")
    fun checkForTutor(
            @Query("user") userId: Long
    ): Call<Tutor>

    @GET("/room/{roomId}/")
    fun checkForStudent(
            @Path("roomId") roomId: Long
    ): Call<Int>

    @GET("/room/{roomId}/tutor-token")
    fun getTutorToken(
            @Path("roomId") roomId: Long
    ): Call<Opentok>


    @GET("/room/{roomId}/student-token")
    fun getStudentToken(
            @Path("roomId") roomId: Long
    ): Call<Opentok>
}