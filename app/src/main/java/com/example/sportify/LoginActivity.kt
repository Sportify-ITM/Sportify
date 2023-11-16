package com.example.sportify

import android.content.Context
import kotlinx.coroutines.launch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var firestore: FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "GoogleLogin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        firebaseAuth = FirebaseManager.authInstance
        firestore = FirebaseFirestore.getInstance()
        Log.d("TAGGG", "${firebaseAuth.currentUser.toString()}")
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IsLoggedIn", false)
        if (isLoggedIn && firebaseAuth.currentUser != null) {
            goToLoginSuccessActivity()
        }
        val googleLoginButton = findViewById<Button>(R.id.btnGoogle) // Assuming you have a button with this ID in your XML
        googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        CoroutineScope(Dispatchers.IO).launch {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed: ${e.statusCode}", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("IsLoggedIn", true).apply()
                    val user = firebaseAuth.currentUser
                    user?.let{
                        updateUserToFirebase(it.uid, it.displayName, it.email)
                    }
                    goToLoginSuccessActivity()
                    // Update UI with the signed-in user's information
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // Handle failure
                }
            }
    }

    private fun updateUserToFirebase(userId: String, name: String?, email: String?){
        val userMap = hashMapOf(
            "name" to name,
            "email" to email
        )
        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "User details updated in Firestore")
            }
            .addOnFailureListener{e ->
                Log.d(TAG, "User details updated in Firestore")
            }

    }
    private fun goToLoginSuccessActivity() {
        // Intent to start Home Activity or update UI
        val intent = Intent(this, LoginSuccessActivity::class.java)
        startActivity(intent)
        finish() // Close this activity
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
