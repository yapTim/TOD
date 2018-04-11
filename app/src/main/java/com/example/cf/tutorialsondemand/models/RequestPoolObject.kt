package com.example.cf.tutorialsondemand.models

import com.google.gson.annotations.SerializedName

data class RequestPoolObject(
        @SerializedName("id") val poolId: Long,
        @SerializedName("user") val userId: Long,
        @SerializedName("category") val categoryId: Int,
        val status: Int
)