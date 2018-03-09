package com.example.cf.tutorialsondemand

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeTransform
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        student.setOnClickListener {
            startActivity(Intent(this, AskActivity::class.java))
            finish()
        }

        tutor.setOnClickListener {
            startActivity(Intent(this, SelectQuestionsActivity::class.java))
            finish()
        }

        livestream.setOnClickListener {
            startActivity(Intent(this, LivestreamActivity::class.java))
            finish()
        }

        chat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
            finish()
        }
    }
}