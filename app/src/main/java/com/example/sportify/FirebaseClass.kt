package com.example.sportify

import com.google.firebase.auth.FirebaseAuth

class FirebaseClass {
}

object FirebaseManager {
    val authInstance: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}