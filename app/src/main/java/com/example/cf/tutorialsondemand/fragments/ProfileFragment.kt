package com.example.cf.tutorialsondemand.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.TestLooperManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.activities.LoginActivity
import com.facebook.login.LoginManager
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG1 = "title"
private const val ARG2 = "page"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
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

        logoutBtn.setOnClickListener {
            signOutFacebook()
        }

        return view
    }

    fun removeLoginPreference() {
        // Shared Preference
        val loginPreference = this.activity?.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
        with (loginPreference?.edit()) {
            this?.clear()?.apply()
        }

    }

    private fun signOutFacebook() {
        AlertDialog.Builder(this.activity)
                .setTitle(getString(R.string.logOutAlertTitle))
                .setMessage(getString(R.string.logOutAlertMessage))
                .setPositiveButton("OK", DialogInterface.OnClickListener {_, _ ->
                    removeLoginPreference()
                    LoginManager.getInstance().logOut()
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
