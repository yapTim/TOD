package com.example.cf.tutorialsondemand.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.SignalMessage
import java.util.*

var viewTypeLocal: Int = 0
var viewTypeRemote: Int = 1
lateinit var viewTypes: Map<Int, Int>

class SignalAdapter(context: Context) : ArrayAdapter<SignalMessage>(context, 0)  {
    companion object {
        init {
            val aMap = hashMapOf(viewTypeLocal to R.layout.local_message_layout)
            aMap[viewTypeRemote] = R.layout.remote_message_layout
            viewTypes = Collections.unmodifiableMap(aMap)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val message: SignalMessage = getItem(position)
        var convertViewNew = convertView
        val messageTextView = convertViewNew?.findViewById<TextView>(R.id.message_text)!!

        if(convertView == null) {
            val type: Int = getItemViewType(position)
            convertViewNew = LayoutInflater.from(context).inflate(viewTypes.get(type)!!, null)
        }

        messageTextView.text = message.messageText

        return convertViewNew
    }

    override fun getItemViewType(position: Int): Int {
        val message: SignalMessage = getItem(position)
        return if(message.remote) { viewTypeRemote } else { viewTypeLocal }
    }

    override fun getViewTypeCount(): Int = viewTypes.size

}