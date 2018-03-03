package com.example.cf.tutorialsondemand

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_waiting.*

class WaitingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)
        val i = getIntent()
        qstnText.text = getString(R.string.genText, i.getCharSequenceExtra("Question"))
    }
}
