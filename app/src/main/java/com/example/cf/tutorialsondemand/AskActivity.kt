package com.example.cf.tutorialsondemand

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cf.tutorialsondemand.R.id.question
import kotlinx.android.synthetic.main.activity_ask.*

class AskActivity : AppCompatActivity() {
    val c: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask)

        btn.setOnClickListener {
            val i = Intent(this, WaitingActivity::class.java)
            i.putExtra("Question", question.text)
            startActivity(i)
            finish()
        }
    }
}
