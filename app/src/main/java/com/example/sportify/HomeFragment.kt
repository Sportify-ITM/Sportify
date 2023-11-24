import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportify.MatchCardAdapter
import com.example.sportify.R
import com.example.sportify.TeamTableAdapter
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
    val time: String = "",
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
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

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
                updateMatchRecyclerView(matchesData, view)
                updateStandingsRecyclerView(standingsData, view)
            } else {
                Log.e("ITM", "Data not found in Firebase")
            }
        }
        return view
    }

    private fun updateMatchRecyclerView(matchData: ArrayList<MatchTeamItem>?, view: View) {
        matchData?.let {
            val manager1 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter1 = MatchCardAdapter(it)
            val recyclerHorizon = view.findViewById<RecyclerView>(R.id.recyclerHorizon)
            recyclerHorizon.apply {
                adapter = adapter1
                layoutManager = manager1
            }
        }
    }

    private fun updateStandingsRecyclerView(standingsData: ArrayList<TeamTable>?, view: View) {
        standingsData?.let {
            val manager2 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val adapter2 = TeamTableAdapter(it)
            val recyclerVertical = view.findViewById<RecyclerView>(R.id.recyclerVertical)
            recyclerVertical.apply {
                adapter = adapter2
                layoutManager = manager2
            }
        }
    }
}

