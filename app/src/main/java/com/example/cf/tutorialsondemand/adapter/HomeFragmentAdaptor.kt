package com.example.cf.tutorialsondemand.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.cf.tutorialsondemand.fragments.CommunityFragment
import com.example.cf.tutorialsondemand.fragments.ProfileFragment
import com.example.cf.tutorialsondemand.fragments.SelectActionFragment

class HomeFragmentAdaptor(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    val numFragments = 3
    private val tabTitles = mutableListOf("Home", "Community", "Profile")

    override fun getCount(): Int = numFragments

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 ->  return SelectActionFragment.newInstance(position, "title for activity $position")
            1 ->  return CommunityFragment.newInstance("", "")
            2 ->  return ProfileFragment.newInstance("Title $position", 3)
            else -> return null
        }

    }

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}