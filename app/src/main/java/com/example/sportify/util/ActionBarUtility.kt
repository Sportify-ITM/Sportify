package com.example.sportify.util

import androidx.appcompat.app.AppCompatActivity

object ActionBarUtility {

    fun setLogo(activity: AppCompatActivity, logoResId: Int) {
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        activity.supportActionBar?.setIcon(logoResId)
        activity.supportActionBar?.setIcon(logoResId)
    }

}
