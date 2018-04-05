package com.example.cf.tutorialsondemand.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView

import com.example.cf.tutorialsondemand.R
import kotlinx.android.synthetic.main.activity_waiting.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*

class WaitingActivity : AppCompatActivity() {

    companion object {
        val handler: Handler = Handler()
    }

    private val timer = Timer()
    private var counter: Int = 0
    private var waitMessage: TextView? = null
    private var mode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)



        if (intent.getStringExtra("action") == "ask") {

            mode = "Tutor"

        } else if (intent.getStringExtra("action") == "answer") {

            mode = "Student"

        }

        cancelButton.setOnClickListener {

            alert(getString(R.string.cancelFindAlertMessage)) {
                title = getString(R.string.cancelFindAlertTitle)
                yesButton {
                    val nextScreen = Intent(this@WaitingActivity, HomeActivity::class.java)
                    nextScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(nextScreen)
                    timer.purge()
                    timer.cancel()
                    finish()
                }
                noButton {

                }
            }.show()
        }

        waitMessage = question_text

        startSearch()
    }

    private fun startSearch() {
        val search = object: TimerTask() {
            override fun run() {
                handler.post({
                    try {
                        when (counter++ % 4) {
                            0 -> waitMessage?.text = getString(R.string.waitingMessage, mode)
                            1 -> waitMessage?.text = getString(R.string.waitingMessage, "$mode.")
                            2 -> waitMessage?.text = getString(R.string.waitingMessage, "$mode. .")
                            3 -> waitMessage?.text = getString(R.string.waitingMessage, "$mode. . .")
                        }

                    } catch (e: Exception) {
                        toast(e.toString())
                    }
                })
            }
        }

        timer.schedule(search, 0, 1000)
    }
}
