package com.example.cf.tutorialsondemand.models

/**
 * Created by CF on 3/7/2018.
 */
data class SignalMessage @JvmOverloads constructor(var messageText: String, var remote: Boolean = false)