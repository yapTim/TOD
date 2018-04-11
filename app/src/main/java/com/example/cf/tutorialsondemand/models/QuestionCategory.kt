package com.example.cf.tutorialsondemand.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class QuestionCategory(
        @SerializedName("id") val categoryId: Int,
        @SerializedName("text") val categoryLabel: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(categoryId)
        parcel.writeString(categoryLabel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuestionCategory> {
        override fun createFromParcel(parcel: Parcel): QuestionCategory {
            return QuestionCategory(parcel)
        }

        override fun newArray(size: Int): Array<QuestionCategory?> {
            return arrayOfNulls(size)
        }
    }
}