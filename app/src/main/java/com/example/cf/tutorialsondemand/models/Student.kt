package com.example.cf.tutorialsondemand.models

import com.google.gson.annotations.SerializedName

data class Student(
        @SerializedName("last_name") val lastName: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("id") val poolId: Long,
        @SerializedName("user") val studentId: Long,
        val status: Int,
        @SerializedName("category") val categoryId: Int,
        @SerializedName("avatar") val profilePicture: String
)