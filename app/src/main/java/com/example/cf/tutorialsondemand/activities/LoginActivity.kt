package com.example.cf.tutorialsondemand.activities

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.R.id.loginGoogle
import com.example.cf.tutorialsondemand.R.id.logoutButton
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.facebook.*
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
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    private val email: String = "email"
    private val profile = "public_profile"
    private var callbackManager: CallbackManager? = null
    private val RC_SIGN_IN = 33

    // Google Login API
    private lateinit var signInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginFacebook()
        loginGoogle()

    }

    override fun onStart() {

        googleSignInClient = GoogleSignIn.getClient(this, signInOptions)
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)

        if (googleAccount != null) {

            Log.i(this@LoginActivity::class.simpleName, "It's not null")
            logoutGoogle.visibility = View.VISIBLE
            loginGoogle.isEnabled = false

//            startActivity(Intent(this,SelectActionActivity::class.java))
        } else {

            Log.i(this@LoginActivity::class.simpleName, "It's null")

        }

        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // Google Login Methods

    private fun loginGoogle() {

        //Google

        val logoutBtn: Button = findViewById(R.id.logoutButton)
        logoutBtn.visibility = View.GONE

        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_backend_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, signInOptions)


        loginGoogle.setSize(SignInButton.SIZE_STANDARD)

        loginGoogle.setOnClickListener {
            signInGoogle()
        }

        logoutGoogle.setOnClickListener {
            signOutGoogle()
        }

        bypassButton.setOnClickListener {
            startActivity(Intent(this, SelectActionActivity::class.java))
        }
    }

    private fun signInGoogle() {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount> ) {

        try {

            Log.i(this@LoginActivity::class.simpleName, "It's in loler")
            val account = completedTask.getResult(ApiException::class.java)
            Log.i(this@LoginActivity::class.simpleName, "It is: ${account.email}")

            if(account.idToken != null) {
                Log.i(this@LoginActivity::class.simpleName, "It's I logged in")
                sendGoogleToken(account.idToken!!)
            }

            //startActivity(Intent(this, SelectActionActivity::class.java))

        } catch (e: ApiException) {

            Log.e(this@LoginActivity::class.simpleName, "Error Here: ${e.message}")

        }

    }

    private fun sendGoogleToken(token: String) {

        val conn = Connect(getString(R.string.url)).connectionGoogle.loginGoogle(token)
        conn.enqueue(object: Callback<Any> {

            override fun onResponse(call: Call<Any>?, response: Response<Any>?) {

                val respo = response?.body()

                if (respo != null) {
                    Log.i(LoginActivity::class.simpleName, "It was: $respo")
                } else {
                    Log.i(LoginActivity::class.simpleName, "Null was returned")
                }

                logoutGoogle.visibility = View.VISIBLE
                loginGoogle.isEnabled = false
                setLoginPreference(1, "google")
            }

            override fun onFailure(call: Call<Any>?, t: Throwable?) {

                Log.e(LoginActivity::class.simpleName, "sendGoogleToken Retrofit Error: ${t.toString()}")
                if (t?.message == "unexpected end of stream"){sendGoogleToken(token)}

            }

        })

    }

    private fun signOutGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this@LoginActivity, object: OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                Log.i(LoginActivity::class.simpleName, "I logged out")
                logoutGoogle.visibility = View.GONE
                loginGoogle.isEnabled = true
            }
        })
    }

    // Facebook Login Methods

    private fun loginFacebook() {

        // Facebook Login API

        callbackManager = CallbackManager.Factory.create()

        val fbLoginButton = findViewById<LoginButton>(R.id.loginFacebook)
        fbLoginButton.setReadPermissions(Arrays.asList(email, profile))

        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {

                val accessToken = result?.accessToken?.token!!
                Log.d(LoginActivity::class.simpleName, " the token is: $accessToken")

                sendFacebookToken(accessToken)

            }

            override fun onCancel() {

                val toast = Toast.makeText(this@LoginActivity, "Login Cancelled", Toast.LENGTH_SHORT)
                toast.show()

            }

            override fun onError(error: FacebookException?) {

                val toast = Toast.makeText(this@LoginActivity, "Login Error", Toast.LENGTH_SHORT)
                toast.show()

            }
        })
    }

    fun sendFacebookToken(accessToken: String) {

        val connection = Connect(getString(R.string.url))
                .connectionFacebook
                .loginFacebook(accessToken)

        connection.enqueue(object : Callback<Any> {

            override fun onResponse(call: Call<Any>?, response: Response<Any>) {
                val respo = response.body().toString()
                Log.d(LoginActivity::class.simpleName, "It was $respo")
                setLoginPreference(1, "facebook")
            }

            override fun onFailure(call: Call<Any>?, t: Throwable?) {
                Log.e(LoginActivity::class.simpleName, "It was an error ${t.toString()}")
                if (t?.message == "unexpected end of stream"){sendFacebookToken(accessToken)}
            }

        })

    }

    fun setLoginPreference(userId: Int, method: String) {

        // Shared Preference
        val loginPreference = this@LoginActivity.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
        with (loginPreference.edit()) {
//            putString("accessToken", userId)
            putString("loginApi", method)
            apply()
        }

    }

    fun removeLoginPreference(userId: Int, method: String) {

        // Shared Preference
        val loginPreference = this@LoginActivity.getSharedPreferences(getString(R.string.login_preference_key), Context.MODE_PRIVATE)
        with (loginPreference.edit()) {
            //            putString("accessToken", userId)
            putString("loginApi", method)
            apply()
        }

    }
}
