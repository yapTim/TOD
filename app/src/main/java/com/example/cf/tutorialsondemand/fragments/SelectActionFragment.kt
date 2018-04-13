package com.example.cf.tutorialsondemand.fragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.activities.AskActivity
import com.example.cf.tutorialsondemand.models.User
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_select_action.*

private const val ARG_PARAM1 = "someTitle"
private const val ARG_PARAM2 = "somePage"

class SelectActionFragment : Fragment() {

    private var title: String = " "
    private var page: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_PARAM2, title)
            page = it.getInt(ARG_PARAM1, page)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_select_action, container, false)

        val profile = Gson().fromJson(activity!!
                .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                .getString("profile", ""), User::class.java)

        view.findViewById<TextView>(R.id.selectActionWelcome).text = getString(R.string.welcomeText, profile.firstName)
        val askCardView = view.findViewById<CardView>(R.id.askCard)
        val answerCardView = view.findViewById<CardView>(R.id.answerCard)

        askCardView.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCards, null))
        askCardView.setOnClickListener {
            val intent = Intent(context, AskActivity::class.java)
            intent.putExtra("action", "ask")
            startActivity(intent)
        }

        answerCardView.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorCards, null))
        answerCardView.setOnClickListener {
            val intent = Intent(context, AskActivity::class.java)
            intent.putExtra("action", "answer")
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int, param2: String) =
                SelectActionFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
