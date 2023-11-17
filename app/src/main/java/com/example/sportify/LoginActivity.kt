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

    // 액티비티가 생성될 때 호출되는 함수입니다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        firebaseAuth = FirebaseManager.authInstance
        firestore = FirebaseFirestore.getInstance()
        Log.d("TAGGG", "${firebaseAuth.currentUser.toString()}")

        // 로그인 상태를 확인하기 위한 SharedPreferences 설정
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IsLoggedIn", false)

        // 이미 로그인되어 있다면 로그인 성공 화면으로 이동
        if (isLoggedIn && firebaseAuth.currentUser != null) {
            NavigateUtility().goToLoginSuccessActivity(this)
            // 만약 로그아웃을 하고 싶다면 위에 코드를 사용, 그리고 SIGNOUT FOR DEV 클릭 후 재실행
//            NavigateUtility().goToMainActivity(this)
            finish()
        }

        // Google 로그인 버튼 설정
        val googleLoginButton = findViewById<Button>(R.id.btnGoogle)
        googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Google 로그인을 위한 함수입니다.
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

    // Google 로그인 결과를 처리하는 함수입니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google 로그인 실패: ${e.statusCode}", e)
            }
        }
    }

    // Google 로그인 인증 정보를 Firebase로 전달하는 함수입니다.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:성공")
                    val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("IsLoggedIn", true).apply()
                    val user = firebaseAuth.currentUser
                    user?.let{
                        updateUserToFirebase(it.uid, it.displayName, it.email)
                    }
                    NavigateUtility().goToLoginSuccessActivity(this)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:실패", task.exception)
                }
            }
    }

    // Firebase에 사용자 정보를 업데이트하는 함수입니다.
    private fun updateUserToFirebase(userId: String, name: String?, email: String?){
        val userMap = hashMapOf(
            "name" to name,
            "email" to email
        )
        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "Firestore에 사용자 정보 업데이트 성공")
            }
            .addOnFailureListener{e ->
                Log.d(TAG, "Firestore에 사용자 정보 업데이트 실패")
            }
    }

    // 로그인 성공 후 화면으로 이동하는 함수입니다.


    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
