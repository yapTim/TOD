package com.example.cf.tutorialsondemand

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.example.cf.tutorialsondemand.Objects.Opentok
import com.example.cf.tutorialsondemand.OtherThings.ToastMaker
import com.example.cf.tutorialsondemand.Retrofit.Connect
import com.opentok.android.*
import com.opentok.android.Publisher.CameraListener
import kotlinx.android.synthetic.main.activity_livestream.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//For livestream
private var API_KEY: String? = "46067082"
private var SESSION_ID: String? = "1_MX40NjA2NzA4Mn5-MTUyMDA2MzIxNTcyOH5OZFlFZlU2U3hYWC9WUktHbFVPQklrUGl-UH4"
private var TOKEN: String? = "T1==cGFydG5lcl9pZD00NjA2NzA4MiZzaWc9NTNkMmI5OTJjNjYwZDRjYTY5Zjk2MzM2M2Y0MzZhYWNjY2IyZDQ5YTpzZXNzaW9uX2lkPTFfTVg0ME5qQTJOekE0TW41LU1UVXlNREEyTXpJeE5UY3lPSDVPWkZsRlpsVTJVM2hZV0M5V1VrdEhiRlZQUWtsclVHbC1VSDQmY3JlYXRlX3RpbWU9MTUyMDA2MzI0MSZub25jZT0wLjM1MDM0ODA2NDE5Mjc0NTY3JnJvbGU9cHVibGlzaGVyJmV4cGlyZV90aW1lPTE1MjAxNDk2MzkmaW5pdGlhbF9sYXlvdXRfY2xhc3NfbGlzdD0="
private val LOG_TAG = MainActivity::class.simpleName
private const val RC_VIDEO_APP_PERM = 124

class LivestreamActivity : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener, CameraListener {
    val c: Context = this
    //For livestream
    private var mSession: Session? = null
    private var mPublisher: Publisher? = null
    private var mSubscriber: Subscriber? = null
    private var publisher: FrameLayout? = null
    private var subscriber: FrameLayout? = null

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

    //for livestream permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        val perms: Array<String> = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

        if(EasyPermissions.hasPermissions(this, *perms)) {
            //init view from layout
            publisher = findViewById(R.id.pubsView)
            subscriber = findViewById(R.id.subsView)

            //init and connect session
            mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
            mSession?.setSessionListener(this)
            mSession?.connect(TOKEN)
        } else {
            EasyPermissions.requestPermissions(this, "We need access to your mic and camera", RC_VIDEO_APP_PERM, *perms)
        }
    }

    //Main function
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
        dropCall.setOnTouchListener(mDelayHideTouchListener)
        switchCam.setOnTouchListener(mDelayHideTouchListener)
        muteMic.setOnTouchListener(mDelayHideTouchListener)

        //drop call button
        dropCall.setOnClickListener {
            dropCall()
        }

        //switch cam button
        switchCam.setOnClickListener {
            mPublisher?.cycleCamera()
        }

        //mute mic button
        muteMic.setOnClickListener{
            mPublisher?.publishAudio = mPublisher?.publishAudio != true
        }

        val conn = Connect("http://192.168.254.124")
        val call = conn.connection.getOpentokIds()

//        call.enqueue(object: Callback<Opentok> {
//            override fun onResponse(call: Call<Opentok>?, response: Response<Opentok>?) {
//                val ids  = response?.body()
//
//
//                SESSION_ID = ids?.sessionId
//                TOKEN = ids?.opentokToken
//
//                //start Livestream
//                requestPermissions()
//            }
//
//            override fun onFailure(call: Call<Opentok>?, t: Throwable?) {
//                val i = Intent(c, MainActivity::class.java)
//                "Problems with Server!".showToast(c)
//                startActivity(i)
//
//            }
//        })

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

    //Session
    override fun onConnected(session: Session?) {
        Log.i(LOG_TAG, "Connected")

        mPublisher = Publisher.Builder(this).build()
        mPublisher?.setPublisherListener(this)

        publisher?.addView(mPublisher?.view)
        mSession?.publish(mPublisher)
    }

    override fun onDisconnected(session: Session?) {
        Log.i(LOG_TAG, "Disconnected")
        dropCall()
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        Log.i(LOG_TAG, "Dropped")

        if (mSubscriber != null) {
            mSubscriber = null
            subscriber?.removeAllViews()
            dropCall()
        }
    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.i(LOG_TAG, "Received")

        if(mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession?.subscribe(mSubscriber)
            subscriber?.addView(mSubscriber?.getView())
        }
    }

    override fun onError(session: Session?, error: OpentokError?) {
        Log.i(LOG_TAG, "Error" + error.toString())
    }

    //PublisherKit
    override fun onStreamCreated(publisher: PublisherKit?, stream: Stream?) {
        Log.i(LOG_TAG, "Publisher on Stream Created")
    }

    override fun onStreamDestroyed(publisher: PublisherKit?, stream: Stream?) {
        Log.i(LOG_TAG, "Publisher Stream Destroyed")

        dropCall()
    }

    override fun onError(publisher: PublisherKit?, error: OpentokError?) {
        Log.i(LOG_TAG, "Publisher Error ${error.toString()}")
    }

    //Camera Listener
    override fun onCameraChanged(publisher: Publisher?, cameraId: Int) {
        "I changed cam".showToast(this)
    }

    override fun onCameraError(p0: Publisher?, p1: OpentokError?) {

    }

    fun dropCall() {
        mSession?.disconnect()
        val i = Intent(this, MainActivity::class.java)
        "Call dropped.".showToast(this)
        startActivity(i)
        finish()
    }
}

//extension function for toast
fun Any.showToast(c: Context) {
    Toast.makeText(c, this.toString(), Toast.LENGTH_LONG).show()
}