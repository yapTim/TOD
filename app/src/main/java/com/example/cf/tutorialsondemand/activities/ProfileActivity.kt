package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.RatingBar
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.User
import com.google.gson.Gson
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setToolbar()
        setFields()

    }

    private fun setToolbar() {
        val toolbar = find<Toolbar>(R.id.profileToolbar)
        toolbar.title = "Profile"
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setFields() {

        val profile = Gson().fromJson(
                intent.getStringExtra("profile"),
                User::class.java
        )

        val rating = if (profile.rating == 0.0) "None" else profile.rating.toString()

        find<TextView>(R.id.profilePublicName).text = getString(R.string.profileName, profile.firstName, profile.lastName)
        find<TextView>(R.id.profilePublicEmail).text = getString(R.string.profileEmail, profile.email)
        find<TextView>(R.id.profilePublicDateJoined).text = getString(R.string.profilePublicDateJoined, getDate(profile.dateJoined))
        find<RatingBar>(R.id.profilePublicRatingStars).rating = profile.rating.toFloat()
        find<TextView>(R.id.profilePublicRatingValue).text = rating
        Picasso.get().load(profile.profilePicture).into(find<CircularImageView>(R.id.profilePublicPicture))

    }

    private fun getDate(date: String): String {

        val year = date.subSequence(0, 4)
        val monthNumber = date.subSequence(5, 7).toString().toInt()
        val day = date.subSequence(8, 10)
        lateinit var month: String

        when (monthNumber) {
            1 -> month = "January"
            2 -> month = "February"
            3 -> month = "March"
            4 -> month = "April"
            5 -> month = "May"
            6 -> month = "June"
            7 -> month = "July"
            8 -> month = "August"
            9 -> month = "September"
            10 -> month = "October"
            11 -> month = "November"
            12 -> month = "December"
        }

        return "$month $day, $year"

    }
}
