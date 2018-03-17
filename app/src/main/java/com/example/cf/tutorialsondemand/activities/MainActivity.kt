package com.example.cf.tutorialsondemand.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.cf.tutorialsondemand.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        student.setOnClickListener {
            startActivity(Intent(this, AskActivity::class.java))
        }

        tutor.setOnClickListener {
            startActivity(Intent(this, SelectQuestionsActivity::class.java))
        }

        livestream.setOnClickListener {
            startActivity(Intent(this, LivestreamActivity::class.java))
        }

        chat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("Do you want to exit?")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    finish()
                })
                .setNegativeButton("BACK", DialogInterface.OnClickListener { dialog, which -> })
                .create()
                .show()
    }
}