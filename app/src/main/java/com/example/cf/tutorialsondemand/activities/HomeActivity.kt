package com.example.cf.tutorialsondemand.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.HomeFragmentAdaptor
import kotlinx.android.synthetic.main.activity_select_action.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

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
//                Toast.makeText(this@HomeActivity, "Selected page is: $position", Toast.LENGTH_SHORT).show()
                if (position == 2) {
                    currentFragment = fragmentView.findViewById(R.id.profileFragmentView)
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

    override fun onBackPressed() {}

}
