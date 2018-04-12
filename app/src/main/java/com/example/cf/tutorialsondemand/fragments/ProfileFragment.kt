package com.example.cf.tutorialsondemand.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.activities.LoginActivity
import com.example.cf.tutorialsondemand.models.User
import com.facebook.login.LoginManager
import com.google.gson.Gson
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import java.io.InputStream
import okhttp3.*
import java.io.IOException


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG1 = "title"
private const val ARG2 = "page"

class ProfileFragment : Fragment() {

    private var title: String = ""
    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG1)
            page = it.getInt(ARG2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val logoutBtn = view.findViewById<Button>(R.id.logoutButton)

        val profile = Gson().fromJson(this.activity
                ?.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                ?.getString("profile", ""), User::class.java)

        val rating = if (profile.rating == 0.0) "None" else profile.rating.toString()

        view.findViewById<TextView>(R.id.profileNameText).text = getString(R.string.profileName, profile.firstName, profile.lastName)
        view.findViewById<TextView>(R.id.profileEmailText).text = getString(R.string.profileEmail, profile.email)
        view.findViewById<TextView>(R.id.profileRatingValue).text = rating
        view.findViewById<RatingBar>(R.id.profileRatingStars).rating = profile.rating.toFloat()
        Picasso.get().load(profile.profilePicture).into(view.findViewById<CircularImageView>(R.id.profilePicture))

        logoutBtn.setOnClickListener {
            signOutFacebook()
        }

        return view

    }

    private fun removeLoginPreference() {

        // Shared Preference
        if (this.activity != null) {
            val loginPreference = this.activity?.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
            with (loginPreference?.edit()) {
                this?.clear()?.apply()
            }
        }

    }

    private fun signOutFacebook() {

        AlertDialog.Builder(this.activity)
                .setTitle(getString(R.string.logOutAlertTitle))
                .setMessage(getString(R.string.logOutAlertMessage))
                .setPositiveButton("OK", DialogInterface.OnClickListener {_, _ ->

                    removeLoginPreference()
                    LoginManager.getInstance().logOut()

                    val nextActivty = Intent(this.activity, LoginActivity::class.java)
                    nextActivty.putExtra("loggedOut", true)
                    startActivity(nextActivty)
                    activity?.finish()

                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener {_, _ ->

                })
                .create()
                .show()

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG1, param1)
                        putInt(ARG2, param2)
                    }
                }
    }
}
