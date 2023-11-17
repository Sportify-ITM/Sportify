package com.example.sportify
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


//매치 데이터를 위한 데이터 클래스 Item 정의
data class MatchTeamItem(val matchTime: String, val homeTeam: String, val awayTeam: String)

// 팀 순위를 보여주기 위한 테이블에 들어갈 데이터 정의
data class TeamTable(val rank: Int, val teamName: String, val matchesPlayed: Int, val wins: Int, val draws: Int, val losses: Int, val point: Int)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* 매치 데이터 리사이클러 뷰 파트 */
        // 더미 매치 데이터 하나 생성 (for Recycler View)
        val matchTeamItemList = arrayListOf(
            MatchTeamItem("14:00", "Arsenal", "Bournemouth"),
            MatchTeamItem("16:00", "Tottenham", "Man City"),
            MatchTeamItem("19:00", "Chelsea", "Wolverhampton"),
            MatchTeamItem("01:00", "Aston Villa", "Burnley"),

            )
        // 매치 데이터 리사이클러 뷰를 위한 레이아웃 매니저와 어댑터 준비
        val manager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter1 = MatchCardAdapter(matchTeamItemList)
        // 매치 데이터 리사이클러 뷰 만들고 메인 레이아웃에 있는 recyclerHorizon과 연결하고 레이아웃 매니저, 어댑터 연결
        val recyclerHorizon = findViewById<RecyclerView>(R.id.recyclerHorizon)
        recyclerHorizon.apply {
            adapter = adapter1
            layoutManager = manager1
        }
        /* 매치 데이터 리사이클러 뷰 파트 끝 */

        /* 팀 순위 테이블 리사이클러 뷰 파트 시작 */
        // 더미 팀 순위 데이터 20개 생성 (for Recycler View)
        val teamTableList = arrayListOf(
            TeamTable(1, "Arsenal", 12, 10, 2, 0, 27),
            TeamTable(2, "Man City", 12, 10, 1, 1, 26),
            TeamTable(3, "Chelsea", 12, 9, 2, 1, 25),
            TeamTable(4, "Liverpool", 12, 9, 1, 2, 24),
            TeamTable(5, "Tottenham", 12, 8, 2, 2, 23),
            TeamTable(6, "Wolverhampton", 12, 7, 3, 2, 23),
        )
        // 매치 데이터 리사이클러 뷰를 위한 레이아웃 매니저와 어댑터 준비
        val manager2 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter2 = TeamTableAdapter(teamTableList)
        // 매치 데이터 리사이클러 뷰 만들고 메인 레이아웃에 있는 recyclerHorizon과 연결하고 레이아웃 매니저, 어댑터 연결
        val recyclerVertical = findViewById<RecyclerView>(R.id.recyclerVertical)
        recyclerVertical.apply {
            adapter = adapter2
            layoutManager = manager2
        }
        // 여기부터 API로 EPL 데이터 불러오는 파트
        GlobalScope.launch(Dispatchers.Main) {
            val response = fetchDataFromApi()
            Log.d("ITM", "$response")
        }
    }
    // 네트워크 리퀘스트하는 함수
    private suspend fun fetchDataFromApi(): String {
        return try {
            withContext(Dispatchers.IO) {
                //
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("https://premier-league-standings1.p.rapidapi.com/")
                    .get()
                    .addHeader("X-RapidAPI-Key", "2eb0ad9717msh5a898288a798d61p1803ffjsn8c47cfe397c2")
                    .addHeader("X-RapidAPI-Host", "premier-league-standings1.p.rapidapi.com")
                    .build()

                val response = client.newCall(request).execute()
                //
                response.body?.string() ?: ""
            }
        } catch (e: Exception) {
            Log.e("ITM", "Error during network request", e)
            ""
        }
    }
}