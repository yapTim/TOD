package com.example.cf.tutorialsondemand.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.CardAdapter
import com.example.cf.tutorialsondemand.retrofit.Connect
import kotlinx.android.synthetic.main.activity_ask.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask)

        val categoryGrid: GridView = findViewById(R.id.categoryView)
        val categoryList = listOf("Science", "Math", "English")

        categoryGrid.adapter = CardAdapter(this, categoryList)
    }

    fun getCategories(): List<String> {
        var categories = listOf("")

        val conn = Connect(getString(R.string.url))
                .connectionCategory
                .getCategory()

        conn.enqueue(object: Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>?, response: Response<List<String>>?) {
                val returnedList = response?.body()!!
                categories = returnedList
            }

            override fun onFailure(call: Call<List<String>>?, t: Throwable?) {
                Log.e(AskActivity::class.simpleName, t.toString())
            }
        })

        return categories
    }
}
