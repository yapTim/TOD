package com.example.cf.tutorialsondemand.models

import com.google.gson.annotations.SerializedName

data class TutorCategory(@SerializedName("user") val userId: String,
                         @SerializedName("category") val categoryList: List<String>,
                         @SerializedName("status") val isAvailable: String
)