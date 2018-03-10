package com.example.cf.tutorialsondemand.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cf.tutorialsondemand.R
import kotlinx.android.synthetic.main.activity_ask.*

class AskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask)

        submit_btn.setOnClickListener {
            val i = Intent(this, WaitingActivity::class.java)
            i.putExtra("question", question.text)
            startActivity(i)
        }
    }
}
