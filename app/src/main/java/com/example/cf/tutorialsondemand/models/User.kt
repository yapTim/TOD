package com.example.cf.tutorialsondemand.models

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("user_id") val userId: Long,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        val email: String,
        @SerializedName("avatar") val profilePicture: String,
        var rating: Double,
        @SerializedName("date_joined") val dateJoined: String
)