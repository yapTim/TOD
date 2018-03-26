package com.example.cf.tutorialsondemand.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.R.layout.category_card
import org.w3c.dom.Text

/**
 * Created by CF on 3/26/2018.
 */
class CardAdapter(val context: Context, val categoryList: List<String>) : BaseAdapter() {

    override fun getCount() = categoryList.size

    override fun getItem(position: Int): Any? = categoryList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.category_card, parent, false)
        }

        val category: String = this.getItem(position).toString()

        val newTextView: TextView = view?.findViewById(R.id.categoryHeader)!!

        newTextView.text = category

        return view
    }

}