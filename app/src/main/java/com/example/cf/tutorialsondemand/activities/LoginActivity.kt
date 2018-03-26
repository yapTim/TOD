package com.example.cf.tutorialsondemand.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.cf.tutorialsondemand.R
import com.example.cf.tutorialsondemand.retrofit.Connect
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    val EMAIL: String = "email"
    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        callbackManager = CallbackManager.Factory.create()

        val fbLoginButton = findViewById<LoginButton>(R.id.loginFacebook)
        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL))

        fbLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val accessToken = result?.accessToken?.token
                Log.d(LoginActivity::class.simpleName, " the token is: $accessToken")

//                val connection = Connect(getString(R.string.url))
//                        .connectionFacebook
//                        .loginFacebook(accessToken!!)
//
//                connection.enqueue(object : Callback<Boolean> {
//                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>) {
//                        val respo = response.body().toString()
//                        Log.d(LoginActivity::class.simpleName, "It was $respo")
//                    }
//
//                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
//                        Log.e(LoginActivity::class.simpleName, "It was an error ${t.toString()}")
//                    }
//                })

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

        loginGoogle.setOnClickListener {
            startActivity(Intent(this, SelectActionActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}
