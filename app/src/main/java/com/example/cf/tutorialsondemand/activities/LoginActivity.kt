package com.example.cf.tutorialsondemand.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cf.tutorialsondemand.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.FacebookException
import com.facebook.FacebookSdk

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
                Log.d(LoginActivity::class.simpleName, accessToken)
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}
