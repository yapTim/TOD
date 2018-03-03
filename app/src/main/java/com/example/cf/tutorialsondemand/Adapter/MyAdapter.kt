package com.example.cf.tutorialsondemand.Adapter

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.Objects.Question

class MyAdapter(private val q: List<Question>?, private val c: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = q?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(q != null){
            val txt = TextView(c)
            txt.text = q[position].toString()
            holder.mLL.addView(txt)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val mLL: LinearLayout = v.findViewById(R.id.linear)
        val r: Resources = v.resources
    }
}