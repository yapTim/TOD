package com.example.cf.tutorialsondemand.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cf.tutorialsondemand.R
import kotlinx.android.synthetic.main.activity_waiting.*

class WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        question_text.text = getString(R.string.genText, intent.getCharSequenceExtra("question"))
    }
}
