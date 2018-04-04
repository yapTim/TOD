package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.HomeFragmentAdaptor
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_select_action.*

class SelectActionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_action)

        val adapter = HomeFragmentAdaptor(supportFragmentManager, this@SelectActionActivity)
        fragmentView.adapter = adapter

        fragmentTab.setupWithViewPager(fragmentView)

        fragmentView.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                Toast.makeText(this@SelectActionActivity, "Selected page is: $position", Toast.LENGTH_SHORT).show()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        welcome.text = getString(R.string.welcomeText, "Test")

        askCard.setOnClickListener {
            val intent = Intent(this, AskActivity::class.java)
            intent.putExtra("action", "ask")
            startActivity(intent)
        }

        answerCard.setOnClickListener {
            val intent = Intent(this, AskActivity::class.java)
            intent.putExtra("action", "answer")
            startActivity(intent)
        }
    }
}
