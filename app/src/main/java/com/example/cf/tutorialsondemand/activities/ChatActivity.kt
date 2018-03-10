package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.SignalAdapter
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
    var session: Session? = null
    val logTag = ChatActivity::class.simpleName
    lateinit var editText: EditText
    lateinit var listText: ListView
    lateinit var messageHistory: SignalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageHistory = SignalAdapter(this)
        editText = findViewById(R.id.message_edit_text)
        listText = findViewById(R.id.message_history_list_view)

        listText.adapter = messageHistory

        editText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                val inputmanager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputmanager.hideSoftInputFromWindow(v.windowToken, 0)
                sendMessage()
                true
            } else {
                false
            }
        }

        val conn = Connect(getString(R.string.url))
        val returnCall = conn.connection.getOpentokIds()

        returnCall.enqueue(object: Callback<Opentok> {
            override fun onResponse(call: Call<Opentok>?, response: Response<Opentok>?) {
                val opentokKeys = response?.body()!!

                initializeSession(opentokKeys.apiKey, opentokKeys.sessionId, opentokKeys.accessToken)
            }

            override fun onFailure(call: Call<Opentok>?, t: Throwable?) {
                Log.e(logTag, "Retrofit Error: ${t.toString()}")
            }
        })
    }

    fun initializeSession(apiKey: String, sessionId: String, accessToken: String) {
        session = Session.Builder(this@ChatActivity, apiKey, sessionId).build()
        session?.setSessionListener(this@ChatActivity)
        session?.setSignalListener(this@ChatActivity)
        session?.connect(accessToken)
    }

    private fun sendMessage() {
        Log.d(logTag, "Send Message")
        val signal = SignalMessage(editText.text.toString())
        session?.sendSignal(signalType, signal.messageText)

        editText.setText("")
    }

    private fun showMessage(messageData: String, remote: Boolean) {
        Log.d(logTag, "Show Message")

        val message = SignalMessage(messageData, remote)
        messageHistory.add(message)
    }

    fun logOpentokError(opentokError: OpentokError) {
        Log.e(logTag, "Error Domain: ${opentokError.errorDomain.name}")
        Log.e(logTag, "Error Code: ${opentokError.errorCode.name}")
    }

    // alert dialog

    fun showConfigError(alertTitle: String, errorMessage: String) {
        Log.e(logTag, "Error $alertTitle: $errorMessage")

        AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    run {
                        this@ChatActivity.finish()
                    }
                })
                .show()
    }

    // activity life cycles

    override fun onPause() {
        Log.d(logTag, "onPause")
        super.onPause()

        if(session != null) {
            session?.onResume()
        }
    }

    override fun onResume() {
        Log.d(logTag, "onResume")
        super.onResume()

        if(session != null) {
            session?.onResume()
        }

    }

    //Session Listener

    override fun onConnected(session: Session?) {
        Log.i(logTag, "Connected")
    }

    override fun onDisconnected(session: Session?) {
        Log.i(logTag, "Disconnected")
    }

    override fun onError(session: Session?, error: OpentokError?) {
        Log.i(logTag, "Error")
    }

    override fun onStreamDropped(session: Session?, stream: Stream?) {
        Log.i(logTag, "Connected")
    }

    override fun onStreamReceived(session: Session?, stream: Stream?) {
        Log.i(logTag, "Connected")
    }

    // signal listener

    override fun onSignalReceived(session: Session?, type: String?, data: String?, connection: Connection?) {
        val remote = connection?.equals(session?.connection)?.not()

        if (type != null && type == signalType) {
            showMessage(data!!, remote!!)
        }
    }



}
