package com.example.cf.tutorialsondemand

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var i : Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        student.setOnClickListener {
            i = Intent(this, AskActivity::class.java)
            startActivity(i)
            finish()
        }

        tutor.setOnClickListener {
            i = Intent(this, SelectQuestionsActivity::class.java)
            startActivity(i)
            finish()
        }

        livestream.setOnClickListener {
            i = Intent(this, LivestreamActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}