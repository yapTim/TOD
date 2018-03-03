package com.example.cf.tutorialsondemand.OtherThings

import android.content.Context
import android.widget.Toast

/**
 * Created by CF on 3/3/2018.
 */
class ToastMaker {
    fun Any.showToast(c: Context) {
        Toast.makeText(c, this.toString(), Toast.LENGTH_LONG).show()
    }
}

//
//
//
//
//