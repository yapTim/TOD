package com.example.cf.tutorialsondemand.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.activities.LivestreamActivity
import com.example.cf.tutorialsondemand.activities.ProfileActivity
import com.example.cf.tutorialsondemand.adapter.UserSearchAdaptor
import com.example.cf.tutorialsondemand.models.User
import com.example.cf.tutorialsondemand.retrofit.Connect
import kotlinx.android.synthetic.main.fragment_community.view.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CommunityFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_community, container, false)
        val mainView = view.findViewById<FrameLayout>(R.id.mainView)

        view.searchButton.setOnClickListener {

            val searchString = view.find<EditText>(R.id.searchBar).text.toString()

            view.find<RecyclerView>(R.id.userList).apply {
                setHasFixedSize(true)

                layoutManager = LinearLayoutManager(activity)

                val conn = Connect(getString(R.string.url))
                        .connectionProfile
                        .getSearchResult(searchString)

                conn.enqueue(object: Callback<List<User>> {

                    override fun onResponse(call: Call<List<User>>?, response: Response<List<User>>?) {
                        val returnedListOfUsers = response?.body()!!

                        if(returnedListOfUsers.isEmpty()) {
                            activity?.toast("LOL")
                        }

                        adapter = UserSearchAdaptor(response?.body()!!, activity!!)
                    }

                    override fun onFailure(call: Call<List<User>>?, t: Throwable?) {
                        Log.e(activity!!::class.simpleName, "getSearchResult Error: $t")
                    }

                })

            }

        }


        view.findViewById<Button>(R.id.buttonToInfalte).setOnClickListener {

            layoutInflater.inflate(R.layout.activity_livestream, mainView, true)

        }

        view.find<Button>(R.id.checkProfileButton).setOnClickListener {
            startActivity(Intent(context, LivestreamActivity::class.java))
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CommunityFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
