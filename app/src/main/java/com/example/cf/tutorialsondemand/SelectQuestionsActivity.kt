package com.example.cf.tutorialsondemand

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.example.cf.tutorialsondemand.adapter.QuestionListAdapter
import com.example.cf.tutorialsondemand.models.Question
import com.example.cf.tutorialsondemand.retrofit.Connect
import kotlinx.android.synthetic.main.activity_select_questions.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectQuestionsActivity : AppCompatActivity() {
    val c: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_questions)

        val conn = Connect("http://192.168.254.124:8000")
        val call = conn.connection.getQuestions()

        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(c, LinearLayout.VERTICAL, false)

        call.enqueue(object: Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                val questions: List<Question> = response.body()!!
                recycler.adapter = QuestionListAdapter(questions)
            }

            override fun onFailure(call: Call<List<Question>>?, t: Throwable?) {
                txtHere.text = t.toString()
            }
        })
    }
}
