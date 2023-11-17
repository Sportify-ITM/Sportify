package com.example.sportify

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginSuccessActivity: AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_success)

            val successButSingOutBtn = findViewById<Button>(R.id.SignOutDevBtn)
            val goToMainBtn = findViewById<Button>(R.id.goToLoginPageButton)
            successButSingOutBtn.setOnClickListener{
                Log.d("TAGGG", "before: ${FirebaseManager.authInstance.toString()}")
                signOut()
                Log.d("TAGGG", "after: ${FirebaseManager.authInstance.toString()}")
            }
            goToMainBtn.setOnClickListener {
                NavigateUtility().goToMainActivity(this)
                finish()
            }
        }
    private fun signOut() {
//        firebaseAuth.signOut()
        FirebaseManager.authInstance.signOut()
        // Optionally, also sign out from Google if you're using Google Sign-In
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
    }
}