package com.example.sportify.util

import android.content.Context
import android.content.Intent
import com.example.sportify.MainActivity
import com.example.sportify.StartActivity

class NavigateUtility{
    // StartActivity로 이동한다.
    fun goToStartActivity(context: Context) {
        val intent = Intent(context, StartActivity::class.java)
        context.startActivity(intent)
    }
    // MainActivity로 이동한다.
    fun goToMainActivity(context: Context){
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}