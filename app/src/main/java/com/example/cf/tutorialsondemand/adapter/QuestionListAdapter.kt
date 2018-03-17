package com.example.cf.tutorialsondemand.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.Question

class QuestionListAdapter(private val q: List<Question>) : RecyclerView.Adapter<QuestionListAdapter.ViewHolder>(){
    var questionList = q

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val new_view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(new_view)
    }

    override fun getItemCount() = q.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text_view.text = q[position].text
        //holder.linear_layout.addView(holder.text_view)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val linear_layout: LinearLayout = v.findViewById(R.id.linear)
        val text_view: TextView = v.findViewById(R.id.card_text_view)
    }

    fun setData(list: List<Question>) {
        this.questionList = list
    }
}