package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.CardAdapter
import com.example.cf.tutorialsondemand.models.QuestionCategory
import com.example.cf.tutorialsondemand.retrofit.Connect
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AskActivity : AppCompatActivity() {
    private var categoryCounter = 0
    private lateinit var toolbar: android.support.v7.widget.Toolbar
    private var currentCard: View? = null
    private var currentId: Int = 0
    private var categoryList: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask)

        val action = intent.getStringExtra("action")

        toolbar = findViewById(R.id.categoryToolbar)
        toolbar.title = "Select a Category"
        toolbar.inflateMenu(R.menu.category_actionbar)

        if(action == "answer") {
            toolbar.menu.findItem(R.id.selectCategoryCount).title = getString(R.string.categoryCount, 0)
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.selectCategoryButton -> {
                    when (action) {
                        "answer" -> startQueueTutor()
                        "ask" -> startQueueStudent()
                    }
                }
            }
            true
        }

        val categoryGrid: GridView = findViewById(R.id.categoryView)

        val conn = Connect(getString(R.string.url))
                .connectionCategory
                .getCategory()

        conn.enqueue(object: Callback<List<QuestionCategory>> {

            override fun onResponse(call: Call<List<QuestionCategory>>, response: Response<List<QuestionCategory>>) {
                val returnedList = response.body()!!
                categoryGrid.adapter = CardAdapter(this@AskActivity, returnedList)

                categoryGrid.setOnItemClickListener { _, view, position, _ ->

                    when (action) {
                        "answer" -> {
                            if (categoryList.contains(position+1)) {

                                view.background = null
                                view.background = ResourcesCompat.getDrawable(resources, R.drawable.category_unselected_border, null)
                                categoryList.remove(position + 1)
                                toolbar.menu.findItem(R.id.selectCategoryCount).title = getString(R.string.categoryCount, --categoryCounter)

                            } else {

                                view.background = ResourcesCompat.getDrawable(resources, R.drawable.category_selected_border, null)
                                toolbar.menu.findItem(R.id.selectCategoryCount).title = getString(R.string.categoryCount, ++categoryCounter)
                                categoryList.add(position + 1)

                            }

                            Toast.makeText(this@AskActivity, "categoryList: $categoryList", Toast.LENGTH_LONG).show()
                        }

                        "ask" -> {
                            if (currentCard == null) {

                                currentCard = view

                            } else {

                                val ex = currentCard
                                ex?.background = null
                                ex?.background = ResourcesCompat.getDrawable(resources, R.drawable.category_unselected_border, null)
                                currentCard = view

                            }

                            currentId = position + 1

                            Toast.makeText(this@AskActivity, "position: $currentId", Toast.LENGTH_LONG).show()
                            view.background = ResourcesCompat.getDrawable(resources, R.drawable.category_selected_border, null)
                        }
                    }

                }

            }

            override fun onFailure(call: Call<List<QuestionCategory>>?, t: Throwable?) {
                Log.e(AskActivity::class.simpleName, t.toString())
            }

        })

    }

    private fun startQueueStudent() {

        val conn = Connect(getString(R.string.url))
                .connectionCategory
                .sendStudentCategory(this@AskActivity
                        .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                        .getInt("userId", 0)
                        , currentId
                        , 0)

        conn.enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                val returnedObject = response?.body()
                Log.i(AskActivity::class.simpleName, "This was returned: ${returnedObject.toString()}")

                if(returnedObject == true) {
                    val nextActivity = Intent(this@AskActivity, WaitingActivity::class.java)
                    nextActivity.putExtra("action", "ask")
                    startActivity(nextActivity)
                    finish()
                } else {
                    toast("Something went wrong! Try again!")
                }

            }

            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                Log.e(AskActivity::class.simpleName, "Error: ${t.toString()}")
            }
        })
    }

    private fun startQueueTutor() {

        val conn = Connect(getString(R.string.url))
                .connectionCategory
                .sendTutorCategory(this@AskActivity
                        .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                        .getInt("userId", 0)
                        , categoryList.toIntArray()
                        , 0)

        conn.enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                val returnedObject = response?.body()

                if(returnedObject == true) {
                    val nextActivity = Intent(this@AskActivity, WaitingActivity::class.java)
                    nextActivity.putExtra("action", "answer")
                    startActivity(nextActivity)
                    finish()
                } else {
                    toast("Something went wrong! Try again!")
                }

            }

            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                Log.e(AskActivity::class.simpleName, "Error: ${t.toString()}")
            }
        })

    }

}
