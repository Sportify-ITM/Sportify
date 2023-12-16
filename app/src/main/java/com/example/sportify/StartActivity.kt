package com.example.sportify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sportify.databinding.ActivityStartBinding
import com.example.sportify.db.AppDatabase
import com.example.sportify.db.TeamEntity
import com.example.sportify.util.ActionBarUtility
import com.example.sportify.util.NavigateUtility
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var firestore: FirebaseFirestore

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        // Initialize binding by inflating the layout
        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupClickListeners()

        val goToLoginPageButton = binding.goToLoginPageButton
        goToLoginPageButton.setOnClickListener {
            //firebaseAuth.signOut()
            FirebaseManager.authInstance.signOut()
            // Optionally, also sign out from Google if you're using Google Sign-In
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }

        val currentUser = FirebaseManager.authInstance.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            // 이제 userId를 사용하여 필요한 작업 수행
            Log.d("UserInfo", "User ID: $userId")
        } else {
            Log.d("UserInfo", "No user is currently logged in.")
        }
    }

    private fun setupClickListeners() {
        val teamsLogos = arrayOf(
            binding.icArsenal, binding.icAston, binding.icBournemouth,
            binding.icBrighton, binding.icBrentford, binding.icBurnley,
            binding.icChelsea, binding.icCrystalPalace, binding.icEverton,
            binding.icFulham, binding.icLiverpool, binding.icLutonTown,
            binding.icManchesterCity, binding.icManchesterUnited, binding.icNewcastle,
            binding.icNottingham, binding.icSheffield, binding.icTottenham,
            binding.icWestham, binding.icWolves
        )

        val teamIds = arrayOf(
            R.drawable.ic_arsenal, R.drawable.ic_aston_villa, R.drawable.ic_bournemouth,
            R.drawable.ic_brighton, R.drawable.ic_brentford, R.drawable.ic_burnley,
            R.drawable.ic_chelsea, R.drawable.ic_crystal_palace, R.drawable.ic_everton,
            R.drawable.ic_fulham, R.drawable.ic_liverpool, R.drawable.ic_luton_town,
            R.drawable.ic_man_city, R.drawable.ic_man_united, R.drawable.ic_newcastle,
            R.drawable.ic_nottingham, R.drawable.ic_sheffield, R.drawable.ic_tottenham,
            R.drawable.ic_west_ham, R.drawable.ic_wolverhampton
        )

        val currentUser = FirebaseManager.authInstance.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            // 이제 userId를 사용하여 필요한 작업 수행
            Log.d("UserInfo", "User ID: $userId")
        } else {
            Log.d("UserInfo", "No user is currently logged in.")
        }

        for (teamId in teamIds) {
            Log.d("Team", "Team ID: $teamId")
        }

        teamsLogos.forEachIndexed { index, logo ->
            logo.setOnClickListener {
                updateLogoToFirebase(currentUser!!.uid, teamIds[index])
                saveSelectedTeamToDatabase(teamIds[index])
                NavigateUtility().goToMainActivity(this)
            }
        }
    }

    private fun saveSelectedTeamToDatabase(teamId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val teamEntity = TeamEntity(id = 1, teamId = teamId)
            val database = AppDatabase.getInstance(applicationContext)
            database.teamDao().insertTeam(teamEntity)
            ActionBarUtility.setLogo(this@StartActivity, teamId)
        }
    }

    private fun updateLogoToFirebase(userId: String, logo: Int){

        val logoMap = hashMapOf(
            "logo" to logo as Object
        )

        firestore.collection("logos").document(userId)
            .set(logoMap)
            .addOnSuccessListener {
                Log.d("Logo", "Firestore에 사용자 로고 정보 업데이트 성공")
            }
            .addOnFailureListener{e ->
                Log.d("Logo", "Firestore에 사용자 로고 업데이트 실패")
            }
    }
}
