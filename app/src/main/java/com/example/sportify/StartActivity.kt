package com.example.sportify


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportify.databinding.ActivityStartBinding
import com.example.sportify.db.AppDatabase
import com.example.sportify.entity.TeamEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.appcompat.app.AppCompatActivity
import com.example.sportify.util.NavigateUtility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize the binding object
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arsenalLogo = binding.icArsenal
        arsenalLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_arsenal)
        }

        val astonVillaLogo = binding.icAston
        astonVillaLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_aston_villa)
        }

        val bournemouthLogo = binding.icBournemouth
        bournemouthLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_bournemouth)
        }

        val brightonLogo = binding.icBrighton
        brightonLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_brighton)
        }

        val brentfordLogo = binding.icBrentford
        brentfordLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_brentford)
        }

        val burnleyLogo = binding.icBurnley
        burnleyLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_burnley)
        }

        val chelseaLogo = binding.icChelsea
        chelseaLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_chelsea)
        }

        val crystalPalaceLogo = binding.icCrystalPalace
        crystalPalaceLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_crystal_palace)
        }

        val evertonLogo = binding.icEverton
        evertonLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_everton)
        }

        val fulhamLogo = binding.icFulham
        fulhamLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_fulham)
        }

        val liverpoolLogo = binding.icLiverpool
        liverpoolLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_liverpool)
        }

        val lutonTownLogo = binding.icLutonTown
        lutonTownLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_luton_town)
        }

        val manCityLogo = binding.icManchesterCity
        manCityLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_man_city)
        }

        val manchesterUnitedLogo = binding.icManchesterUnited
        manchesterUnitedLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_man_united)
        }

        val newcastleLogo = binding.icNewcastle
        newcastleLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_newcastle)
        }

        val nottinghamLogo = binding.icNottingham
        nottinghamLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_nottingham)
        }

        val sheffieldLogo = binding.icSheffield
        sheffieldLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_sheffield)
        }

        val tottenhamLogo = binding.icTottenham
        tottenhamLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_tottenham)
        }

        val westhamLogo = binding.icWestham
        westhamLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_west_ham)
        }

        val wolvesLogo = binding.icWolves
        wolvesLogo.setOnClickListener {
            saveSelectedTeamToDatabase(R.drawable.ic_wolverhampton)
        }

        val goToLoginPageButton = binding.goToLoginPageButton
        goToLoginPageButton.setOnClickListener {
            //firebaseAuth.signOut()
            FirebaseManager.authInstance.signOut()
            // Optionally, also sign out from Google if you're using Google Sign-In
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }
    private fun saveSelectedTeamToDatabase(teamId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val teamEntity = TeamEntity(teamId = teamId)
            val database = AppDatabase.getInstance(applicationContext)
            database.teamDao().insertTeam(teamEntity)
        }
    }

}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding by inflating the layout
        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupClickListeners()
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

        teamsLogos.forEachIndexed { index, logo ->
            logo.setOnClickListener {
                saveSelectedTeamToDatabase(teamIds[index])
                NavigateUtility().goToMainActivity(this)
            }
        }
    }

    private fun saveSelectedTeamToDatabase(teamId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val teamEntity = TeamEntity(teamId = teamId)
            //val database = AppDatabase.getInstance(applicationContext)
            //database.teamDao().insertTeam(teamEntity)
        }
    }
}
