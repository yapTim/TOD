package com.example.cf.tutorialsondemand.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import kotlinx.android.synthetic.main.activity_select_action.*

class SelectActionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_action)

        welcome.text = getString(R.string.welcomeText, "Test")

        askCard.setOnClickListener {
            startActivity(Intent(this, AskActivity::class.java))
        }

        answerCard.setOnClickListener {
            Toast.makeText(this, "Hello World!", Toast.LENGTH_LONG).show()
        }
    }
}
