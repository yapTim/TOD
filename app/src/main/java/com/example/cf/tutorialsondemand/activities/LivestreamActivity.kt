package com.example.cf.tutorialsondemand.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Path
import android.os.*
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.SignalAdapter
import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.models.SignalMessage
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.opentok.android.*
import com.opentok.android.Publisher.CameraListener
import kotlinx.android.synthetic.main.activity_livestream.*
import org.jetbrains.anko.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LivestreamActivity : AppCompatActivity(),
        Session.SessionListener,
        PublisherKit.PublisherListener,
        CameraListener,
        Session.SignalListener {

    private lateinit var mSession: Session
    private lateinit var mPublisher: Publisher
    private lateinit var mSubscriber: Subscriber
    private var logTag = LivestreamActivity::class.simpleName
    lateinit var editText: EditText
    lateinit var listText: ListView
    lateinit var messageHistory: SignalAdapter
    private val signalType = "text-signal"

    companion object {

        private val AUTO_HIDE = false

        private val AUTO_HIDE_DELAY_MILLIS = 3000

        private val UI_ANIMATION_DELAY = 300

        const val RC_VIDEO_APP_PERM = 124
    }

    private val mHideHandler = Handler()

    private val mShowPart2Runnable = Runnable {
        fullscreen_content_controls.visibility = View.VISIBLE
    }

    private var mVisible: Boolean = false
    private var chatIsOpen = false
    private val mHideRunnable = Runnable { hide() }

    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible && !chatIsOpen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        TransitionManager.beginDelayedTransition(findViewById(R.id.livestreamConstraintLayout))
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false
        mHideHandler.removeCallbacks(mShowPart2Runnable)
    }

    private fun show() {
        mVisible = true
        TransitionManager.beginDelayedTransition(findViewById(R.id.livestreamConstraintLayout))
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    // Main function

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livestream)

        subsView.setOnClickListener { toggle() }

        dropCallButton.setOnTouchListener(mDelayHideTouchListener)
        switchCameraButton.setOnTouchListener(mDelayHideTouchListener)
        muteMicButton.setOnTouchListener(mDelayHideTouchListener)
        toggleVideoButton.setOnTouchListener(mDelayHideTouchListener)

        // drop call button
        dropCallButton.setOnClickListener {
            dropCall()
        }

        // switch cam button
        switchCameraButton.setOnClickListener {
            mPublisher.cycleCamera()
        }

        // mute mic button
        muteMicButton.setOnClickListener{
            mPublisher.publishAudio = mPublisher.publishAudio != true

            if (!mPublisher.publishAudio) {

                muteMicButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_mic_off, null))
                muteMicButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorChatLocal, null))

            } else {

                muteMicButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_mic_on, null))
                muteMicButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorButton, null))

            }

        }

        // toggle video on and off
        toggleVideoButton.setOnClickListener {
            mPublisher.publishVideo = mPublisher.publishVideo != true

            if (!mPublisher.publishVideo) {

                toggleVideoButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_video_off, null))
                toggleVideoButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorChatLocal, null))

            } else {

                toggleVideoButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.icon_video_on, null))
                toggleVideoButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorButton, null))

            }
        }

        chatButton.setOnClickListener {

            chatIsOpen = !chatIsOpen

            TransitionManager.beginDelayedTransition(findViewById(R.id.livestreamConstraintLayout))

            if (chatIsOpen) {

                findViewById<ConstraintLayout>(R.id.chatConstriantLayout).visibility = View.VISIBLE

            } else {

                findViewById<ConstraintLayout>(R.id.chatConstriantLayout).visibility = View.GONE

            }

        }

        requestPermissions()

        messageHistory = SignalAdapter(this)
        editText = findViewById(R.id.message_edit_text)
        listText = find(R.id.message_history_list_view)

        listText.adapter = messageHistory

        editText.setOnEditorActionListener { v, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE && editText.text.toString() != ""){
                val inputmanager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputmanager.hideSoftInputFromWindow(v.windowToken, 0)
                sendMessage()
                true
            } else {
                false
            }
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

    }

    // for livestream permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        val perms: Array<String> = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        if(EasyPermissions.hasPermissions(this, *perms)) {

            when(intent.getStringExtra("action")) {

                "ask" -> {

                    val conn = Connect(getString(R.string.url))
                            .connectionLivestream
                            .getStudentToken(intent.getLongExtra("roomId", 0))

                    conn.enqueue(object: Callback<Opentok> {

                        override fun onResponse(call: Call<Opentok>?, response: Response<Opentok>?) {

                            val keys = response?.body()!!

                            find<Toolbar>(R.id.chatToolbar).title = intent.getStringExtra("tutorName")

                            initializeSession(keys.apiKey, keys.sessionId, keys.accessToken)

                        }

                        override fun onFailure(call: Call<Opentok>?, t: Throwable?) {

                            Log.e(LivestreamActivity::class.simpleName, "getStudentToken Error: $t")

                        }

                    })

                }

                "answer" -> {

                    val conn = Connect(getString(R.string.url))
                            .connectionLivestream
                            .getTutorToken(intent.getLongExtra("roomId", 0))

                    conn.enqueue(object: Callback<Opentok> {

                        override fun onResponse(call: Call<Opentok>?, response: Response<Opentok>?) {
                            val keys = response?.body()!!

                            find<Toolbar>(R.id.chatToolbar).title = intent.getStringExtra("studentName")

                            initializeSession(keys.apiKey, keys.sessionId, keys.accessToken)
                        }

                        override fun onFailure(call: Call<Opentok>?, t: Throwable?) {

                            Log.e(LivestreamActivity::class.simpleName, "getTutorToken Error: $t")

                        }

                    })

                }

            }

        } else {
            EasyPermissions.requestPermissions(this, "We need access to your mic and camera", RC_VIDEO_APP_PERM, *perms)
        }
    }

    // Session
    override fun onConnected(session: Session?) {

        Log.i("LOl", "Connected")

        mPublisher = Publisher.Builder(this).build()
        mPublisher.setPublisherListener(this)

        mSession.publish(mPublisher)
        findViewById<FrameLayout>(R.id.pubsView).addView(mPublisher.view)

        editText.isEnabled = true

    }

    override fun onDisconnected(session: Session?) {

        Log.i(logTag, "Disconnected")

        editText.isEnabled = false

    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {

        Log.i(logTag, "Dropped")
        findViewById<LinearLayout>(R.id.loadingView).visibility = View.VISIBLE

        dropCall()

    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.i(logTag, "Received")

        mSubscriber = Subscriber.Builder(this, stream).build()
        mSession.subscribe(mSubscriber)
        findViewById<LinearLayout>(R.id.loadingView).visibility = View.GONE
        findViewById<FrameLayout>(R.id.subsView).addView(mSubscriber.view)

    }

    override fun onError(session: Session?, error: OpentokError?) {
        Log.i(logTag, error.toString())
    }

    // PublisherKit

    override fun onStreamCreated(publisher: PublisherKit?, stream: Stream?) {
        Log.i(logTag, "Publisher on Stream Created")
    }

    override fun onStreamDestroyed(publisher: PublisherKit?, stream: Stream?) {
        Log.i(logTag, "Publisher Stream Destroyed")

        dropCall()
    }

    override fun onError(publisher: PublisherKit?, error: OpentokError?) {
        Log.i(logTag, "Publisher Error ${error.toString()}")
    }

    // Camera Listener
    override fun onCameraChanged(publisher: Publisher?, cameraId: Int) {
        toast("I changed cam")
    }

    override fun onCameraError(p0: Publisher?, p1: OpentokError?) {

    }

    private fun dropCall() {

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
                                .setRoomToInactive(intent.getLongExtra("roomId", 0))

                        newConnection.enqueue(object: Callback<Boolean> {

                            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                                val roomWasSet = response?.body()!!

                                if (roomWasSet) {

                                    mSession.disconnect()
                                    toast("Call Dropped")
                                    inflateRatingActivity()

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

            mSession.disconnect()
            mPublisher.destroy()
            mSubscriber.destroy()
            toast("Call Dropped")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()

        }

    }

    private fun sendMessage() {

        Log.d(logTag, "Send Message")

        toast("In")

        val signal = SignalMessage(editText.text.toString())
        mSession.sendSignal(signalType, signal.messageText)

        editText.setText("")

    }

    private fun showMessage(messageData: String, remote: Boolean) {

        Log.d(logTag, "Show Message")

        val message = SignalMessage(messageData, remote)
        messageHistory.add(message)

    }

    override fun onSignalReceived(session: Session?, type: String?, data: String?, connection: Connection?) {
        val remote = connection?.equals(session?.connection)?.not()

        if (type != null && type == signalType) {
            showMessage(data!!, remote!!)
        }

    }

    private fun inflateRatingActivity() {
        val mainContainer = find<LinearLayout>(R.id.livestreamContainer)
        mainContainer.removeAllViews()
        mainContainer.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))

        val view = layoutInflater.inflate(R.layout.activity_rating, mainContainer,true)

        view.find<TextView>(R.id.rateTutor).setOnClickListener {

            val rating = view.find<RatingBar>(R.id.tutorRatingBar).rating

            val conn = Connect(getString(R.string.url))
                    .connectionProfile
                    .rateTutor(intent.getLongExtra("tutorId", 0), rating)

            conn.enqueue(object: Callback<Boolean> {

                override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {

                    if(response?.body()!!) {

                        toast("Thank you for rating!")
                        startActivity(Intent(this@LivestreamActivity, HomeActivity::class.java))
                        finish()

                    }

                }

                override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                        Log.e(LivestreamActivity::class.simpleName, "rateTutor Error: $t")
                }

            })

        }

        view.find<TextView>(R.id.noThanksText).setOnClickListener {

            startActivity(Intent(this@LivestreamActivity, HomeActivity::class.java))
            finish()

        }

    }

    private fun initializeSession(apiKey: String, sessionId: String, accessToken: String) {

        mSession = Session.Builder(this@LivestreamActivity, apiKey, sessionId).build()
        mSession.setSessionListener(this@LivestreamActivity)
        mSession.setSignalListener(this@LivestreamActivity)
        mSession.connect(accessToken)

    }

    override fun onBackPressed() {

        alert(getString(R.string.backLivestreamAlertMessage)) {
            title = getString(R.string.backLivestreamAlertTitle)
            yesButton {
                dropCall()
            }

            noButton {

            }
        }.show()

    }
}