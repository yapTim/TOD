package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.HomeFragmentAdaptor
import com.example.cf.tutorialsondemand.models.User
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_select_action.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class HomeActivity : AppCompatActivity() {
    private lateinit var toolbar: android.support.v7.widget.Toolbar
    private var currentFragment: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_action)

        toolbar = findViewById(R.id.homeToolbar)

        val adapter = HomeFragmentAdaptor(supportFragmentManager)
        fragmentView.adapter = adapter

        fragmentTab.setupWithViewPager(fragmentView)

        fragmentView.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {

                if (position == 2) {

                    currentFragment = fragmentView.findViewById(R.id.profileFragmentView)

                    val profile = Gson().fromJson(this@HomeActivity
                            .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                            .getString("profile", ""), User::class.java)

                    val conn = Connect(getString(R.string.url))
                            .connectionProfile
                            .getNewRating(profile.userId)

                    conn.enqueue(object: Callback<Double> {

                        override fun onResponse(call: Call<Double>?, response: Response<Double>?) {
                            if(response?.body()!! != profile.rating) {

                                profile.rating = response.body()!!
                                currentFragment?.find<RatingBar>(R.id.profileRatingStars)?.rating = profile?.rating?.roundTo2DecimalPlaces()?.toFloat()!!
                                currentFragment?.find<TextView>(R.id.profileRatingValue)?.text = profile.rating.roundTo2DecimalPlaces().toString()
                                setLoginPreference(profile)

                            }
                        }

                        override fun onFailure(call: Call<Double>?, t: Throwable?) {

                            Log.e(HomeActivity::class.simpleName, "getNewRating Error: $t")
                        }

                    })

                }

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_SETTLING) {

                }
            }
        })

        toolbar.setOnMenuItemClickListener {

            true
        }

        if(intent.getBooleanExtra("failedAccept", false)) {
            failedAcceptAlert()
        }

        if(intent.getBooleanExtra("failedFind", false)) {
            failedFindAlert()
        }

        if(intent.getBooleanExtra("declined", false)) {
            declinedAlert()
        }

    }

    private fun failedAcceptAlert() {
        alert(getString(R.string.acceptFailedAlertMessage)) {
            title = getString(R.string.acceptFailedAlertTitle)
            yesButton {}
        }.show()
    }

    private fun failedFindAlert() {
        var message = ""

        when(this@HomeActivity.intent.getStringExtra("action")) {
            "ask" -> message = getString(R.string.findFailedAlertMessage, "Tutor")
            "answer" -> message = getString(R.string.findFailedAlertMessage, "Student")
        }

        alert(message) {
            title = getString(R.string.findFailedAlertTitle)
            yesButton {}
        }.show()
    }

    private fun declinedAlert() {
        var message = ""

        when(this@HomeActivity.intent.getStringExtra("action")) {
            "ask" -> message = getString(R.string.declinedAlertMessage, "Tutor")
            "answer" -> message = getString(R.string.declinedAlertMessage, "Student")
        }

        alert(message) {
            title = getString(R.string.declinedAlertTitle)
            yesButton {}
        }.show()
    }

    fun setLoginPreference(user: User) {
        Log.i(HomeActivity::class.simpleName, "This is cool: $user")
        // Shared Preference

        val loginPreference = this@HomeActivity.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
        with (loginPreference.edit()) {
            putLong("userId", user.userId)
            putString("profile", com.google.gson.Gson().toJson(user))
            apply()
        }

    }

    fun Double.roundTo2DecimalPlaces() =
            BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()


    override fun onBackPressed() {}

}
