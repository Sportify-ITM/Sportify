package com.example.sportify
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// 매치 데이터를 위한 데이터 클래스 Item 정의
data class MatchTeamItem(
    val utcDate: String = "",
    val homeTeam: String = "",
    val awayTeam: String = "",
    val status: String = ""
)

// 팀 순위를 보여주기 위한 테이블에 들어갈 데이터 정의
data class TeamTable(
    val rank: Int = -1,
    val teamName: String ="",
    val matchesPlayed: Int = -1,
    val wins: Int = -1,
    val draws: Int = -1,
    val losses: Int = -1,
    val points: Int = -1
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 여기부터 파이어베이스에서 EPL 데이터 불러오는 파트
        GlobalScope.launch(Dispatchers.Main) {
            val database = Firebase.database
            val matches = database.getReference("matches")
            val standings = database.getReference("standings")

            // Retrieve matches data
            val matchesSnapshot = withContext(Dispatchers.IO) {
                matches.get().await()
            }
            // Retrieve standings data
            val standingsSnapshot = withContext(Dispatchers.IO) {
                standings.get().await()
            }

            // Process the data and update the RecyclerViews
            if (matchesSnapshot.exists() && standingsSnapshot.exists()) {
                val matchesData = matchesSnapshot.getValue(object : GenericTypeIndicator<ArrayList<MatchTeamItem>>() {})
                val standingsData = standingsSnapshot.getValue(object : GenericTypeIndicator<ArrayList<TeamTable>>() {})

                // Update RecyclerViews with the fetched data
                updateMatchRecyclerView(matchesData)
                updateStandingsRecyclerView(standingsData)
            } else {
                Log.e("ITM", "Data not found in Firebase")
            }
        }
    }

    private fun updateMatchRecyclerView(matchData: ArrayList<MatchTeamItem>?) {
        matchData?.let {
            val manager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val adapter1 = MatchCardAdapter(it)
            val recyclerHorizon = findViewById<RecyclerView>(R.id.recyclerHorizon)
            recyclerHorizon.apply {
                adapter = adapter1
                layoutManager = manager1
            }
        }
    }

    private fun updateStandingsRecyclerView(standingsData: ArrayList<TeamTable>?) {
        standingsData?.let {
            val manager2 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter2 = TeamTableAdapter(it)
            val recyclerVertical = findViewById<RecyclerView>(R.id.recyclerVertical)
            recyclerVertical.apply {
                adapter = adapter2
                layoutManager = manager2
            }
        }
    }
}
