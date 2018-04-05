package com.example.cf.tutorialsondemand.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.adapter.QuestionListAdapter
import com.example.cf.tutorialsondemand.models.Question
import com.example.cf.tutorialsondemand.retrofit.Connect
import kotlinx.android.synthetic.main.activity_select_questions.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectQuestionsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var adapter: QuestionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_questions)

        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this@SelectQuestionsActivity,
                LinearLayout.VERTICAL,
                false)

        refreshQuestions.setOnRefreshListener(this@SelectQuestionsActivity)

        getQuestionList()
        print("LOL")

    }

    fun getQuestionList() {
//        val conn = Connect(getString(R.string.url))
//        val call = conn.connection.getQuestions()
//
//        call.enqueue(object : Callback<List<Question>> {
//            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
//                val questions: List<Question> = response.body()!!
//                adapter = QuestionListAdapter(questions)
//                recycler.adapter = adapter
//                refreshQuestions.isRefreshing = false
//            }
//
//            override fun onFailure(call: Call<List<Question>>?, t: Throwable?) {
//                txtHere.text = t.toString()
//            }
//        })
    }

    override fun onRefresh() {
        getQuestionList()

    }
}
