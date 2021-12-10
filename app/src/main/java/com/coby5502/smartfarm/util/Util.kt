package com.coby5502.smartfarm.util

import android.widget.Toast
import com.coby5502.smartfarm.MyApplication

class Util {
    companion object{
        fun showNotification(msg: String) {
            Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}