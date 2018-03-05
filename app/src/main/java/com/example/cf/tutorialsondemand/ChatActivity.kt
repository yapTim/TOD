package com.example.cf.tutorialsondemand

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Connection
import com.opentok.android.OpentokError

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val editText: EditText = findViewById(R.id.message_edit_text)
        val listView: ListView = findViewById(R.id.message_history_list_view)
    }
}
