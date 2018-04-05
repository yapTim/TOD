package com.example.cf.tutorialsondemand.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.HomeFragmentAdaptor
import kotlinx.android.synthetic.main.activity_select_action.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_action)

        val adapter = HomeFragmentAdaptor(supportFragmentManager)
        fragmentView.adapter = adapter

        fragmentTab.setupWithViewPager(fragmentView)

        fragmentView.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
//                Toast.makeText(this@HomeActivity, "Selected page is: $position", Toast.LENGTH_SHORT).show()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

}
