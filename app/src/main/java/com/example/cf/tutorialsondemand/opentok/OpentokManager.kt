package com.example.cf.tutorialsondemand.opentok

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.example.cf.tutorialsondemand.MainActivity
import com.example.cf.tutorialsondemand.models.Opentok
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.example.cf.tutorialsondemand.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by CF on 3/4/2018.
 */
class OpentokManager {
    lateinit var api_key: String
    lateinit var session_id: String
    lateinit var token: String

    fun setAuthKeys() {
        val connection = Connect("")
        val returnCall = connection.connection.getOpentokIds()

        returnCall.enqueue(object: Callback<Opentok> {
            override fun onResponse(call: Call<Opentok>, response: Response<Opentok>) {
                val ids = response.body()!!

                api_key = "123456"
                session_id = ids.s_id
                token = ids.access_token
            }

            override fun onFailure(call: Call<Opentok>, t: Throwable?) {

            }
        })
    }
}