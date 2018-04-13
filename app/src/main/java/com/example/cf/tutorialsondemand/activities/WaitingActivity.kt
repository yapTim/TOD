package com.example.cf.tutorialsondemand.activities

import android.app.AlertDialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.R.id.cancelButton
import com.example.cf.tutorialsondemand.models.RequestPoolObject
import com.example.cf.tutorialsondemand.models.Student
import com.example.cf.tutorialsondemand.models.Tutor
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.facebook.login.LoginManager
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import junit.runner.Version.id
import kotlinx.android.synthetic.main.activity_waiting.*
import kotlinx.android.synthetic.main.customalertview_cofirmation_dialog.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class WaitingActivity : AppCompatActivity() {

    companion object {
        val handler: Handler = Handler()
        val handlerForTimer = Handler()
    }

    private var timer = Timer()
    private val clockTimer = Timer()
    private var counter: Int = 0
    private var waitMessage: TextView? = null
    private var mode: String? = null
    lateinit var foundAlert: AlertDialog
    private val timeLimit = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        timeElapsed.text = getString(R.string.timeElapsed,0,0,0)

        if (intent.getStringExtra("action") == "ask") {

            mode = "Tutor"

        } else if (intent.getStringExtra("action") == "answer") {

            mode = "Student"

        }

        cancelButton.setOnClickListener {

            alert(getString(R.string.cancelFindAlertMessage)) {
                title = getString(R.string.cancelFindAlertTitle)

                yesButton {
                    timer.purge()
                    timer.cancel()

                    handler.removeCallbacksAndMessages(null)
                    handlerForTimer.removeCallbacksAndMessages(null)

                    if(intent.getStringExtra("action") == "ask") {

                        val connection = Connect(getString(R.string.url))
                                .connectionCategory
                                .setStudentToInactive(intent.getLongExtra("poolId", 0))

                        connection.enqueue(object: Callback<Boolean> {

                            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {

                                val wasSet = response?.body()!!

                                if (wasSet) {

                                    val nextScreen = Intent(this@WaitingActivity, HomeActivity::class.java)
                                    nextScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(nextScreen)

                                    finish()

                                }

                            }

                            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                                Log.i(WaitingActivity::class.simpleName, "setStudentToInactive Error: $t")
                            }

                        })

                    } else {

                        val nextScreen = Intent(this@WaitingActivity, HomeActivity::class.java)
                        nextScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(nextScreen)

                        finish()

                    }

                }

                noButton {}
            }.show()
        }

        waitMessage = question_text

        startSearch()

    }

    private fun startSearch() {
        var minute = 0
        var secondTens = 0
        var secondOnes = 0

        val search = object: TimerTask() {
            override fun run() {
                handler.post({
                    try {

                        when (counter++ % 4) {
                            0 -> waitMessage?.text = getString(R.string.waitingMessage, mode)
                            1 -> waitMessage?.text = getString(R.string.waitingMessage, "$mode .")
                            2 -> waitMessage?.text = getString(R.string.waitingMessage, "$mode . .")
                            3 -> waitMessage?.text = getString(R.string.waitingMessage, "$mode . . .")
                        }

                        when (intent.getStringExtra("action")) {

                            "ask" -> {
                                Log.i(WaitingActivity::class.simpleName, "This is the ID ${this@WaitingActivity
                                        .getSharedPreferences(
                                                getString(R.string.login_preference_key),
                                                Context.MODE_PRIVATE
                                        ).getLong("userId", 0)}")

                                val conn = Connect(getString(R.string.url))
                                        .connectionLivestream
                                        .checkForTutor(
                                        this@WaitingActivity
                                                .getSharedPreferences(
                                                        getString(R.string.login_preference_key),
                                                        Context.MODE_PRIVATE
                                                ).getLong("userId", 0))

                                conn.enqueue(object: Callback<Tutor> {

                                    override fun onResponse(call: Call<Tutor>?, response: Response<Tutor>?) {
                                        val tutor = response?.body()!!

                                        if (!checkEmptyObject(tutor) && counter < timeLimit) {

                                            timer.purge()
                                            timer.cancel()

                                            clockTimer.purge()
                                            clockTimer.cancel()

                                            handler.removeCallbacksAndMessages(null)
                                            handlerForTimer.removeCallbacksAndMessages(null)

                                            confirmation(tutor)

                                        }

                                    }

                                    override fun onFailure(call: Call<Tutor>?, t: Throwable?) {

                                        Log.e(WaitingActivity::class.simpleName, "checkForTutor Error: $t")

                                    }
                                })
                            }

                            "answer" -> {

                                val conn = Connect(getString(R.string.url)).connectionCategory.sendTutorCategory(intent.getIntArrayExtra("categories"))
                                conn.enqueue(object: Callback<Student> {

                                    override fun onResponse(call: Call<Student>?, response: Response<Student>?) {
                                        val matchedUser = response?.body()!!

                                        if(!checkEmptyObject(matchedUser) && counter < timeLimit) {

                                            timer.purge()
                                            timer.cancel()

                                            clockTimer.purge()
                                            clockTimer.cancel()

                                            handler.removeCallbacksAndMessages(null)
                                            handlerForTimer.removeCallbacksAndMessages(null)

                                            confirmation(matchedUser)

                                        }

                                    }

                                    override fun onFailure(call: Call<Student>?, t: Throwable?) {
                                        Log.e(WaitingActivity::class.simpleName, "FindStudent Error: $t")
                                    }
                                })

                            }

                        }

                        if (counter > timeLimit) {

                            timer.purge()
                            timer.cancel()

                            clockTimer.purge()
                            clockTimer.cancel()

                            handler.removeCallbacksAndMessages(null)
                            handlerForTimer.removeCallbacksAndMessages(null)

                            if(intent.getStringExtra("action") == "ask") {
                                val connection = Connect(getString(R.string.url))
                                        .connectionCategory.setStudentToInactive(intent
                                        .getLongExtra("poolId", 0))

                                connection.enqueue(object: Callback<Boolean> {

                                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                                        val failed = response?.body()!!

                                        val intent = Intent(this@WaitingActivity, HomeActivity::class.java)
                                        intent.putExtra("failedFind", failed)
                                        intent.putExtra("action", this@WaitingActivity.intent.getStringExtra("action"))
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }

                                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {

                                        Log.e(WaitingActivity::class.simpleName, "Error setStudentToInactive: $t")

                                    }

                                })

                            } else {

                                val intent = Intent(this@WaitingActivity, HomeActivity::class.java)
                                intent.putExtra("failedFind", true)
                                intent.putExtra("action", this@WaitingActivity.intent.getStringExtra("action"))
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                startActivity(intent)
                                finish()

                            }

                        }

                    } catch (e: Exception) {

                        toast("ERROR: $e")

                    }
                })
            }
        }

        timer.schedule(search, 0, 1000)

        val timecheck = object: TimerTask() {

            override fun run() {
                handlerForTimer.post {

                    try {

                        if (counter % 60 == 0 && counter != 0) {

                            minute++
                            secondTens = 0
                            secondOnes = 0

                        } else if (counter % 10 == 0 && counter != 0){

                            secondTens++
                            secondOnes = 0

                        } else if (counter != 0) {

                            secondOnes++

                        }

                        timeElapsed.text = getString(R.string.timeElapsed, minute, secondTens, secondOnes)

                    } catch (e: Exception) {

                        Log.e(WaitingActivity::class.simpleName, "Timer Exception: $e")

                    }
                }
            }

        }

        clockTimer.schedule(timecheck, 0, 1000)
    }

    private fun confirmation(matchFound: Any?) {
        counter = 30

        val foundTimer = Timer()
        var matchStudent: Student? = null
        var matchTutor: Tutor? = null

        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.customalertview_cofirmation_dialog, null)

        view.findViewById<TextView>(R.id.foundHeader).text = getString(R.string.foundMessage, mode)
        view.findViewById<TextView>(R.id.timerText).text = counter.toString()

        when (matchFound) {

            is Student -> {
                matchStudent = matchFound
                view.findViewById<TextView>(R.id.matchName).text = getString(R.string.matchName, matchStudent.firstName, matchStudent.lastName)
                Picasso.get().load(matchStudent.profilePicture).into(view.find<CircularImageView>(R.id.matchProfilePicture))
            }

            is Tutor -> {
                matchTutor = matchFound
                view.findViewById<TextView>(R.id.matchName).text = getString(R.string.matchName, matchTutor.firstName, matchTutor.lastName)
                Picasso.get().load(matchTutor.profilePicture).into(view.find<CircularImageView>(R.id.matchProfilePicture))
            }

        }

        val acceptButton = view.findViewById<Button>(R.id.acceptButton)
        val declineButton = view.findViewById<Button>(R.id.declineButton)

        acceptButton.setOnClickListener {

            foundTimer.purge()
            foundTimer.cancel()

            acceptButton.visibility = View.GONE
            declineButton.visibility = View.GONE

            handler.removeCallbacksAndMessages(null)

            when(intent.getStringExtra("action")) {

                "ask" ->{
                    foundAlert.dismiss()

                    val nextActivity = Intent(this@WaitingActivity, LivestreamActivity::class.java)
                    nextActivity.putExtra("roomId", matchTutor?.roomId!!)
                    nextActivity.putExtra("poolId", intent.getLongExtra("poolId", 0))
                    nextActivity.putExtra("tutorId", matchTutor.tutorId)
                    nextActivity.putExtra("tutorName", matchTutor.firstName)
                    nextActivity.putExtra("action", "ask")

                    startActivity(nextActivity)
                    finish()

                }

                "answer" -> {

                    view.findViewById<TextView>(R.id.timerText).visibility = View.GONE
                    view.find<TextView>(R.id.timerTextHeader).visibility = View.GONE

                    val conn = Connect(getString(R.string.url))
                            .connectionLivestream
                            .makeRoom(matchStudent?.categoryId!!,
                                    matchStudent.studentId,
                                    this@WaitingActivity.getSharedPreferences(
                                            getString(R.string.login_preference_key),
                                            Context.MODE_PRIVATE
                                    ).getLong("userId", 0L))

                    conn.enqueue(object: Callback<Long> {

                        override fun onResponse(call: Call<Long>?, response: Response<Long>?) {
                            val roomId = response?.body()!!

                            val waitingForStudentText = view.findViewById<TextView>(R.id.timerText)

                            waitingForStudentText.text = getString(R.string.waitingForStudent)
                            waitingForStudentText.visibility = View.VISIBLE

                            counter = 0
                            waitForStudent(roomId, matchStudent.firstName)
                        }

                        override fun onFailure(call: Call<Long>?, t: Throwable?) {
                            Log.e(WaitingActivity::class.simpleName, "makeRoom Error: $t")
                        }

                    })

                }

            }

        }

        declineButton.setOnClickListener {

            foundTimer.purge()
            foundTimer.cancel()

            handler.removeCallbacksAndMessages(null)
            handlerForTimer.removeCallbacksAndMessages(null)

            foundAlert.dismiss()

            if (intent.getStringExtra("action") == "ask") {

                val connection = Connect(getString(R.string.url))
                        .connectionCategory
                        .setStudentToInactive(intent.getLongExtra("poolId", 0))

                connection.enqueue(object: Callback<Boolean> {

                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                        val studentWasSet = response?.body()!!

                        if (studentWasSet) {

                            val newConnection = Connect(getString(R.string.url))
                                    .connectionCategory
                                    .setRoomToInactive(matchTutor?.roomId!!)

                            newConnection.enqueue(object: Callback<Boolean> {

                                override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                                    val roomWasSet = response?.body()!!

                                    if (roomWasSet) {

                                        val nextActivity = Intent(this@WaitingActivity, HomeActivity::class.java)
                                        nextActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        nextActivity.putExtra("declinedSelf", true)
                                        startActivity(nextActivity)
                                        finish()

                                    }

                                }

                                override fun onFailure(call: Call<Boolean>?, t: Throwable?) {

                                    Log.e(AskActivity::class.simpleName, "setRoomToInactive Error: $t")

                                }
                            })
                        }

                    }

                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {

                        Log.e(AskActivity::class.simpleName, "setStudentToInactive Error: $t")

                    }
                })

            } else {

                val nextActivity = Intent(this@WaitingActivity, HomeActivity::class.java)
                nextActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                nextActivity.putExtra("declinedSelf", true)
                startActivity(nextActivity)
                finish()

            }

        }

        foundAlert = AlertDialog.Builder(this).setView(view).setCancelable(false).create()
        foundAlert.show()

        val wait = object: TimerTask() {
            override fun run() {
                handler.post({
                    try {
                        if (counter > 0) {

                            foundAlert.findViewById<TextView>(R.id.timerText).text = counter--.toString()

                        } else {

                            foundTimer.purge()
                            foundTimer.cancel()
                            foundAlert.dismiss()

                            val intent = Intent(this@WaitingActivity, HomeActivity::class.java)
                            intent.putExtra("failedAccept", true)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                            startActivity(intent)

                        }

                    } catch (e: Exception) {
                        toast(e.toString())
                    }
                })
            }
        }

        foundTimer.schedule(wait, 0, 1000)
    }

    private fun waitForStudent(roomId: Long, studentName: String) {
        timer = Timer()

        val waitForStudent = object: TimerTask() {

            override fun run() {
                handler.post {

                    try {

                        val connection = Connect(getString(R.string.url))
                                .connectionLivestream
                                .checkForStudent(roomId)

                        connection.enqueue(object: Callback<Int> {

                            override fun onResponse(call: Call<Int>?, response: Response<Int>?) {
                                val status = response?.body()!!

                                when(status) {
                                    0 -> {

                                        timer.purge()
                                        timer.cancel()

                                        handler.removeCallbacksAndMessages(null)

                                        val nextActivity = Intent(this@WaitingActivity, HomeActivity::class.java)

                                        nextActivity.putExtra("declined", true)
                                        nextActivity.putExtra("action", intent.getStringExtra("action"))
                                        nextActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                        foundAlert.dismiss()

                                        startActivity(nextActivity)
                                        finish()

                                    }

                                    2 -> {

                                        timer.purge()
                                        timer.cancel()

                                        val nextActivity = Intent(this@WaitingActivity, LivestreamActivity::class.java)
                                        nextActivity.putExtra("roomId", roomId)
                                        nextActivity.putExtra("studentName", studentName)
                                        nextActivity.putExtra("action", "answer")

                                        handler.removeCallbacksAndMessages(null)

                                        foundAlert.dismiss()

                                        startActivity(nextActivity)
                                        finish()

                                    }
                                }
                            }

                            override fun onFailure(call: Call<Int>?, t: Throwable?) {
                                Log.e(WaitingActivity::class.simpleName, "checkForStudent Error: $t")

                            }

                        })

                    } catch (e: Exception) {}

                }
            }
        }

        timer.schedule(waitForStudent, 0, 1000)
    }

    private fun checkEmptyObject(objectToCheck: Any): Boolean {
        var ret  = false

        when(objectToCheck) {
            is Student -> {

                if(objectToCheck.poolId == 0L && objectToCheck.studentId == 0L && objectToCheck.categoryId == 0) {
                    ret = true
                }

            }

            is Tutor -> {

                if ( objectToCheck.roomId == 0L && objectToCheck.firstName == null && objectToCheck.lastName == null) {
                    ret = true
                }

            }
        }

        return ret

    }

    override fun onBackPressed() {}
}
