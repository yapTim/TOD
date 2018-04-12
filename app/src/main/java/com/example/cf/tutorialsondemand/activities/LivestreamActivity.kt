package com.example.cf.tutorialsondemand.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.opentok.android.*
import com.opentok.android.Publisher.CameraListener
import kotlinx.android.synthetic.main.activity_livestream.*
import org.jetbrains.anko.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LivestreamActivity : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener, CameraListener {
    private lateinit var mSession: Session
    private lateinit var mPublisher: Publisher
    private lateinit var mSubscriber: Subscriber
    private var logTag = MainActivity::class.simpleName

    companion object {

        private val AUTO_HIDE = true

        private val AUTO_HIDE_DELAY_MILLIS = 3000

        private val UI_ANIMATION_DELAY = 300

        const val RC_VIDEO_APP_PERM = 124
    }

    private val mHideHandler = Handler()

    private val mShowPart2Runnable = Runnable {
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
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
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false
        mHideHandler.removeCallbacks(mShowPart2Runnable)
    }

    private fun show() {
        mVisible = true
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
        }

        // toggle video on and off
        toggleVideoButton.setOnClickListener {
            mPublisher.publishVideo = mPublisher.publishVideo != true
        }

        requestPermissions()
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

        findViewById<FrameLayout>(R.id.pubsView).addView(mPublisher.view)
        mSession.publish(mPublisher)
    }

    override fun onDisconnected(session: Session?) {
        Log.i(logTag, "Disconnected")

        findViewById<LinearLayout>(R.id.loadingView).visibility = View.VISIBLE
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {

        Log.i(logTag, "Dropped")
        findViewById<FrameLayout>(R.id.subsView).removeAllViews()
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
        mSession.disconnect()
        toast("Call Dropped")
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun initializeSession(apiKey: String, sessionId: String, accessToken: String) {
        mSession = Session.Builder(this@LivestreamActivity, apiKey, sessionId).build()
        mSession.setSessionListener(this@LivestreamActivity)
        mSession.connect(accessToken)
    }
}