package com.example.cf.tutorialsondemand.models

import android.support.v4.media.session.MediaSessionCompat
import com.google.gson.annotations.SerializedName

data class Tutor(
        @SerializedName("id") val roomId: Long,
        @SerializedName("user_tutor") val tutorId: Long,
        @SerializedName("first_name") val firstName: String?,
        @SerializedName("last_name") val lastName: String?,
        @SerializedName("avatar") val profilePicture: String
)