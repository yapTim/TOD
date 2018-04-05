package com.example.cf.tutorialsondemand.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.activities.ChatActivity
import com.example.cf.tutorialsondemand.activities.LivestreamActivity
import com.example.cf.tutorialsondemand.activities.LoginActivity
import com.facebook.login.LoginManager

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG1 = "title"
private const val ARG2 = "page"

class ProfileFragment : Fragment() {

    private var title: String = ""
    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG1)
            page = it.getInt(ARG2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val logoutBtn = view.findViewById<Button>(R.id.logoutButton)
        val chatBtn = view.findViewById<Button>(R.id.chatButton)
        val livestreamBtn = view.findViewById<Button>(R.id.livestreamButton)

        logoutBtn.setOnClickListener {
            signOutFacebook()
        }

        chatBtn.setOnClickListener {
            startActivity(Intent(this.activity, ChatActivity::class.java))
        }

        livestreamBtn.setOnClickListener {
            startActivity(Intent(this.activity, LivestreamActivity::class.java))
        }





        return view
    }

    private fun removeLoginPreference() {

        // Shared Preference
        if (this.activity != null) {
            val loginPreference = this.activity?.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
            with (loginPreference?.edit()) {
                this?.clear()?.apply()
            }
        }

    }

    private fun signOutFacebook() {

        AlertDialog.Builder(this.activity)
                .setTitle(getString(R.string.logOutAlertTitle))
                .setMessage(getString(R.string.logOutAlertMessage))
                .setPositiveButton("OK", DialogInterface.OnClickListener {_, _ ->

                    removeLoginPreference()
                    LoginManager.getInstance().logOut()
                    startActivity(Intent(this.activity, LoginActivity::class.java))
                    activity?.finish()

                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener {_, _ ->

                })
                .create()
                .show()

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: Int) =
                ProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG1, param1)
                        putInt(ARG2, param2)
                    }
                }
    }
}
