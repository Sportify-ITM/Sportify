package com.example.sportify

import android.content.Intent
import android.content.Context
import androidx.core.content.ContextCompat.startActivity

class NavigateUtility{
    // Login_success_activity 로 이동한다.
    fun goToLoginSuccessActivity(context: Context) {
        val intent = Intent(context, LoginSuccessActivity::class.java)
        context.startActivity(intent)
    }
    // MainActivity 로 이동한다.
    fun goToMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
    fun goToNaviActivity(context: Context){
        val intent = Intent(context, NaviActivity::class.java)
        context.startActivity(intent)
    }
}