package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.app.AlertDialog
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.models.QuestionCategory
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import mehdi.sakout.fancybuttons.FancyButton
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val email: String = "email"
    private val profile = "public_profile"
    private var callbackManager: CallbackManager? = null

    companion object {
        val handler = Handler()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fbLoginButton = findViewById<FancyButton>(R.id.loginFacebook)

        handler.postDelayed(object: Runnable {
            override fun run() {
                val constraintSet = ConstraintSet()

                constraintSet.clone(this@LoginActivity, R.layout.activity_login)

                constraintSet.setVerticalBias(R.id.logo, 0.25f)

                val transition = AutoTransition()
                transition.duration = 1000

                TransitionManager.beginDelayedTransition(findViewById(R.id.loginConstraint), transition)
                constraintSet.applyTo(findViewById(R.id.loginConstraint))

                fbLoginButton.setIconResource("\uf082")
                fbLoginButton.visibility = View.VISIBLE
            }
        }, 700)

        loginFacebook()

    }

    override fun onStart() {
        // for Facebook

        if(AccessToken.getCurrentAccessToken() != null && this.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE).contains("userId")) {

            Log.d(LoginActivity::class.simpleName, "It was ${this@LoginActivity
                    .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                    .getLong("userId", 0)}")

            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()

        }

        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    // Facebook Login Methods

    private fun loginFacebook() {

        // Facebook Login API
        val fbLoginButton = findViewById<FancyButton>(R.id.loginFacebook)
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager
                , object: FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {

                val accessToken = result?.accessToken?.token!!
                Log.d(LoginActivity::class.simpleName, " the facebook token is: $accessToken")

                sendFacebookToken(accessToken)

            }

            override fun onCancel() {
                toast("Facebook Login Cancelled")
            }

            override fun onError(error: FacebookException?) {
                toast("Facebook Login Error: $error")
            }

        })

        fbLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList(email, profile))
        }

    }

    fun sendFacebookToken(accessToken: String) {

        val connection = Connect(getString(R.string.url))
                .connectionFacebook
                .loginFacebook(accessToken)

        connection.enqueue(object : Callback<Long> {

            override fun onResponse(call: Call<Long>?, response: Response<Long>) {
                val respo = response.body()!!

                setLoginPreference(respo)

                Log.d(LoginActivity::class.simpleName, "It was ${this@LoginActivity
                        .getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
                        .getLong("userId", 0)}")

                getCategories()

                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()

            }

            override fun onFailure(call: Call<Long>?, t: Throwable?) {
                Log.e(LoginActivity::class.simpleName, "loginFacebook Error: $t")
                if (t?.message == "unexpected end of stream"){
                    sendFacebookToken(accessToken)
                } else {
                    LoginManager.getInstance().logOut()
                    alert(getString(R.string.notConnectedAlertMessage)) {
                        title = getString(R.string.notConnectedAlertTitle)
                        yesButton {}
                    }.show()
                }
            }

        })

    }

    private fun getCategories() {
        val conn = Connect(getString(R.string.url)).connectionCategory.getCategory()
        conn.enqueue(object: Callback<List<QuestionCategory>> {

            override fun onResponse(call: Call<List<QuestionCategory>>?, response: Response<List<QuestionCategory>>?) {
                val returnedList = response?.body()!!
                val categoryPreference = this@LoginActivity.getSharedPreferences(getString(R.string.category_preference_key), Context.MODE_PRIVATE)
                val categoryList: MutableSet<String> = mutableSetOf()

                for (item in returnedList) {
                    categoryList.add(Gson().toJson(item))
                }

                with (categoryPreference.edit()) {
                    putStringSet("categoryList", categoryList)
                    apply()
                }
            }

            override fun onFailure(call: Call<List<QuestionCategory>>?, t: Throwable?) {
                Log.e(LoginActivity::class.simpleName, "Get category Error: $t")
                if (t?.message == "unexpected end of stream"){getCategories()}
            }
        })
    }

    fun setLoginPreference(userId: Long) {

        // Shared Preference
        val loginPreference = this@LoginActivity.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
        with (loginPreference.edit()) {
            putLong("userId", userId)
            apply()
        }

    }

    override fun onBackPressed() {
        alert(getString(R.string.exitAppAlertMessage)) {
            title = getString(R.string.exitAppAlertTitle)
            yesButton { finish() }
            noButton {  }
        }.show()
    }
}
