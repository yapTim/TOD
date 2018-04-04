package com.example.cf.tutorialsondemand.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.cf.tutorialsondemand.fragments.SelectActionFragment

class HomeFragmentAdaptor(fragmentManager: FragmentManager, c: Context) : FragmentPagerAdapter(fragmentManager) {

    val NUM_ITEMS = 3
    private val tabTitles = mutableListOf("Tab1", "Tab2", "Tab3")
    private val context: Context = c

    override fun getCount(): Int = NUM_ITEMS

    override fun getItem(position: Int): Fragment? {
        return SelectActionFragment.newInstance(position+1, "This is a title")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}