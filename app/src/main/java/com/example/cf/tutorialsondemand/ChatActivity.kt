package com.example.cf.tutorialsondemand

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.models.SignalMessage
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Connection
import com.opentok.android.OpentokError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity(), Session.SessionListener, Session.SignalListener {
    val signalType = "text-signal"
    lateinit var session: Session
    val logTag = ChatActivity::class.simpleName

    val editText = findViewById<EditText>(R.id.message_edit_text)
    val listText = findViewById<ListView>(R.id.message_history_list_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        editText.setOnKeyListener(View.OnKeyListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputmanager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputmanager.hideSoftInputFromWindow(v.windowToken, 0)
//                sendMessage()
                Log.d(logTag, "It works")
                return@OnKeyListener true
            }
            false
        })

        editText.isEnabled = false

        val conn = Connect("")
        val returnCall = conn.connection.getOpentokIds()

        returnCall.enqueue(object: Callback<Opentok> {
            override fun onResponse(call: Call<Opentok>?, response: Response<Opentok>?) {
                val opentokKeys = response?.body()!!

                initializeSession(opentokKeys.apiKey, opentokKeys.sessionId, opentokKeys.accessToken)
            }

            override fun onFailure(call: Call<Opentok>?, t: Throwable?) {

            }
        })
    }

    override fun onPause() {
        Log.d(logTag, "onPause")
        super.onPause()
        session.onPause()
    }

    override fun onResume() {
        Log.d(logTag, "onResume")
        super.onResume()
        session.onResume()
    }

    override fun onConnected(session: Session?) {

    }

    override fun onDisconnected(session: Session?) {

    }

    override fun onError(session: Session?, error: OpentokError?) {

    }

    override fun onSignalReceived(session: Session?, type: String?, data: String?, connection: Connection?) {

    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {

    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {

    }

    fun initializeSession(apiKey: String, sessionId: String, accessToken: String) {
        session = Session.Builder(this@ChatActivity, apiKey, sessionId).build()
        session.setSessionListener(this@ChatActivity)
        session.setSignalListener(this@ChatActivity)
        session.connect(accessToken)
    }

    private fun sendMessage() {
        Log.d(logTag, "Send Message")
        val signal = SignalMessage(editText.text.toString())
        session.sendSignal(signalType, signal.messageText)

        editText.setText("")
    }

    private fun showMessage(messageData: String, remote: Boolean) {
        Log.d(logTag, "Show Message")
    }
}