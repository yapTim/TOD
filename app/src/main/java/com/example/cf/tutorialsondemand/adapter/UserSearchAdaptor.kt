package com.example.cf.tutorialsondemand.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.activities.ProfileActivity
import com.example.cf.tutorialsondemand.models.User
import com.google.gson.Gson
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class UserSearchAdaptor(val userList: List<User>, val context: Context) : RecyclerView.Adapter<UserSearchAdaptor.ViewHolder>() {

    class ViewHolder(val cardView:  CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_card, parent, false) as CardView)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get().load(userList[position].profilePicture).into(holder.cardView.find<CircularImageView>(R.id.searchProfilePicture))
        holder.cardView.find<TextView>(R.id.searchUserName).text = context.getString(R.string.profileName, userList[position].firstName, userList[position].lastName)

        holder.cardView.setOnClickListener {
            Log.i(context::class.simpleName, "This is the profile ${userList[position]}")

            val nextActivity = Intent(context, ProfileActivity::class.java)
            nextActivity.putExtra("profile", Gson().toJson(userList[position]))

            context.startActivity(nextActivity)

        }

    }

    override fun getItemCount(): Int = userList.size
}