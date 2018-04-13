package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.R.id.wrap_content
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.CardView
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.QuestionCategory
import com.example.cf.tutorialsondemand.models.RequestPoolObject
import com.example.cf.tutorialsondemand.models.Student
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.google.gson.Gson
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.ceil


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

            if (!categoryList.isEmpty() || action == "ask") {

                if ((action == "ask" && currentId != 0) || action == "answer") {

                    when (it.itemId) {
                        R.id.selectCategoryButton -> {
                            when (action) {
                                "answer" -> startQueueTutor()
                                "ask" -> startQueueStudent()
                            }
                        }
                    }

                } else {

                    toast("No category selected")

                }

            } else {

                toast("No categories selected!")
            }

            true
        }

        val categorySet = this.getSharedPreferences(
                getString(R.string.category_preference_key),
                Context.MODE_PRIVATE
        ).getStringSet("categoryList", null)

        val categoryListCount = categorySet.size

        val rowCount =  ceil(categoryListCount / 2.0).toInt()

        val inflater = this.layoutInflater

        val table: LinearLayout = findViewById(R.id.categoryTable)

        for (i in 0 until rowCount) {
            val tableRow = inflater.inflate(R.layout.category_table_row, table,  false) as LinearLayout

            createCategoryCardRow(inflater, tableRow, categorySet, i, rowCount, action, categoryListCount)
            table.addView(tableRow)

        }

    }

    private fun startQueueStudent() {

        val conn = Connect(getString(R.string.url))
                .connectionCategory
                .sendStudentCategory(this@AskActivity
                        .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                        .getLong("userId", 0)
                        , currentId)

        conn.enqueue(object: Callback<RequestPoolObject> {
            override fun onResponse(call: Call<RequestPoolObject>?, response: Response<RequestPoolObject>?) {
                val returnedObject = response?.body()
                Log.i(AskActivity::class.simpleName, "This was returned: ${returnedObject.toString()}")

                if(returnedObject != null) {

                    val nextActivity = Intent(this@AskActivity, WaitingActivity::class.java)
                    nextActivity.putExtra("action", "ask")
                    nextActivity.putExtra("poolId", returnedObject.poolId)

                    nextActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    nextActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

                    startActivity(nextActivity)
                    finish()

                }

            }

            override fun onFailure(call: Call<RequestPoolObject>?, t: Throwable?) {
                Log.e(AskActivity::class.simpleName, "Error: ${t.toString()}")
                if (t?.message == "unexpected end of stream"){startQueueStudent()}
            }
        })
    }

    private fun startQueueTutor() {

        val conn = Connect(getString(R.string.url))
                .connectionCategory
                .sendTutorCategory(categoryList.toIntArray())

        conn.enqueue(object: Callback<Student> {

            override fun onResponse(call: Call<Student>?, response: Response<Student>?) {

                val nextActivity = Intent(this@AskActivity, WaitingActivity::class.java)
                nextActivity.putExtra("action", "answer")
                nextActivity.putExtra("categories", categoryList.toIntArray())

                startActivity(nextActivity)
                finish()

            }

            override fun onFailure(call: Call<Student>?, t: Throwable?) {
                Log.e(AskActivity::class.simpleName, "Error startQueueTutor: $t")
                if (t?.message == "unexpected end of stream"){startQueueTutor()}
            }
        })

    }

    private fun createCategoryCardRow(inflater: LayoutInflater, tableRow: LinearLayout, categorySet: MutableSet<String>, i: Int, rowCount: Int, action: String, categoryListCount: Int) {
        var categoryCard = inflater.inflate(R.layout.category_card, tableRow, false) as CardView
        var categoryHeader = TextView(this)
        var category = Gson().fromJson(categorySet.elementAt(i*2), QuestionCategory::class.java)

        categoryHeader.width = wrap_content
        categoryHeader.height = wrap_content
        categoryHeader.text = category.categoryLabel
        categoryHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        categoryHeader.gravity = Gravity.CENTER

        categoryCard.background = ResourcesCompat.getDrawable(resources, R.drawable.category_unselected_border, null)
        categoryCard.addView(categoryHeader)
        ViewCompat.setElevation(categoryCard, 3f)

        setCardViewListener(categoryCard, category.categoryId, action)

        tableRow.addView(categoryCard)

        categoryCard = inflater.inflate(R.layout.category_card, tableRow, false) as CardView

        if (categoryListCount % 2 != 1 || i < rowCount-1) {
            categoryHeader = TextView(this)
            category = Gson().fromJson(categorySet.elementAt(i*2+1), QuestionCategory::class.java)

            categoryHeader.width = wrap_content
            categoryHeader.height = wrap_content
            categoryHeader.text = category.categoryLabel
            categoryHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            categoryHeader.gravity = Gravity.CENTER

            categoryCard.background = ResourcesCompat.getDrawable(resources, R.drawable.category_unselected_border, null)
            categoryCard.addView(categoryHeader)
            ViewCompat.setElevation(categoryCard, 3f)

            setCardViewListener(categoryCard, category.categoryId, action)

        } else {

            categoryCard.visibility = View.INVISIBLE

        }

        tableRow.addView(categoryCard)
    }

    private fun setCardViewListener(categoryCard: CardView, id: Int, action: String) {
        categoryCard.setOnClickListener {
            when (action) {
                "answer" -> {
                    if (categoryList.contains(id)) {

                        categoryCard.background = null
                        categoryCard.background = ResourcesCompat.getDrawable(resources, R.drawable.category_unselected_border, null)
                        categoryList.remove(id)
                        toolbar.menu.findItem(R.id.selectCategoryCount).title = getString(R.string.categoryCount, --categoryCounter)

                    } else {

                        categoryCard.background = ResourcesCompat.getDrawable(resources, R.drawable.category_selected_border, null)
                        toolbar.menu.findItem(R.id.selectCategoryCount).title = getString(R.string.categoryCount, ++categoryCounter)
                        categoryList.add(id)

                    }

                }

                "ask" -> {
                    if (currentCard == null) {

                        currentCard = categoryCard

                    } else {

                        val ex = currentCard
                        ex?.background = null
                        ex?.background = ResourcesCompat.getDrawable(resources, R.drawable.category_unselected_border, null)
                        currentCard = categoryCard

                    }

                    currentId = id

                    categoryCard.background = ResourcesCompat.getDrawable(resources, R.drawable.category_selected_border, null)
                }
            }

        }
    }

}
