package com.example.cf.tutorialsondemand

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.opentok.OpentokManager
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.opentok.android.*
import com.opentok.android.Publisher.CameraListener
import kotlinx.android.synthetic.main.activity_livestream.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// For livestream
private const val RC_VIDEO_APP_PERM = 124

class LivestreamActivity : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener, CameraListener {
    // For livestream
    private lateinit var mSession: Session
    private lateinit var mPublisher: Publisher
    private lateinit var mSubscriber: Subscriber
    private var logTag = MainActivity::class.simpleName

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        subsView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
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
            OpentokManager().setAuthKeys()

            mSession = Session.Builder(this, OpentokManager().api_key, OpentokManager().session_id).build()
            mSession.setSessionListener(this)
            mSession.connect(OpentokManager().token)

        } else {
            EasyPermissions.requestPermissions(this, "We need access to your mic and camera", RC_VIDEO_APP_PERM, *perms)
        }
    }

    // Main function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_livestream)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        subsView.setOnClickListener { toggle() }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        drop_call_button.setOnTouchListener(mDelayHideTouchListener)
        switch_camera_button.setOnTouchListener(mDelayHideTouchListener)
        mute_mic_button.setOnTouchListener(mDelayHideTouchListener)
        toggle_video_button.setOnTouchListener(mDelayHideTouchListener)

        // drop call button
        drop_call_button.setOnClickListener {
            dropCall()
        }

        // switch cam button
        switch_camera_button.setOnClickListener {
            mPublisher.cycleCamera()
        }

        // mute mic button
        mute_mic_button.setOnClickListener{
            mPublisher.publishAudio = mPublisher.publishAudio != true
        }

        // toggle video on and off
        toggle_video_button.setOnClickListener {
            mPublisher.publishVideo = mPublisher.publishVideo != true
        }

        requestPermissions()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
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
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        subsView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }

    // Session
    override fun onConnected(session: Session?) {
        Log.i("LOl", "Connected")

        mPublisher = Publisher.Builder(this).build()
        mPublisher.setPublisherListener(this)

        findViewById<FrameLayout>(R.id.pubsView).addView(mPublisher?.view)
        mSession.publish(mPublisher)
    }

    override fun onDisconnected(session: Session?) {
        Log.i(logTag, "Disconnected")

        dropCall()
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        Log.i(logTag, "Dropped")
            findViewById<FrameLayout>(R.id.subsView).removeAllViews()
            dropCall()
    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.i(logTag, "Received")

        if(mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession.subscribe(mSubscriber)
            findViewById<FrameLayout>(R.id.subsView).addView(mSubscriber?.getView())
        }
    }

    override fun onError(session: Session?, error: OpentokError?) {
        Log.i(logTag, "Error" + error.toString())
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
        "I changed cam".showToast(this)
    }

    override fun onCameraError(p0: Publisher?, p1: OpentokError?) {

    }

    private fun dropCall() {
        mSession.disconnect()
        val i = Intent(this, MainActivity::class.java)
        "Call dropped.".showToast(this)
        startActivity(i)
        finish()
    }
}

//extension function for toast
fun String.showToast(c: Context) {
    Toast.makeText(c, this, Toast.LENGTH_LONG).show()
}