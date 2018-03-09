package com.example.cf.tutorialsondemand.models

import com.google.gson.annotations.SerializedName

/**
 * Created by CF on 3/3/2018.
 */
data class Opentok(@SerializedName("session_id") val sessionId: String, @SerializedName("access_token") val accessToken: String, @SerializedName("api_key") val apiKey: String)