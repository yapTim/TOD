package com.example.cf.tutorialsondemand.models

import com.google.gson.annotations.SerializedName

data class QuestionCategory(@SerializedName("id") val categoryId: Int, @SerializedName("text") val categoryLabel: String)