package com.example.sportify

import MatchStatistics
import MatchTeamItem
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.sportify.databinding.FragmentCalenderBinding
import com.example.sportify.databinding.FragmentMatchDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class MatchDetailFragment : Fragment() {

    private lateinit var binding: FragmentMatchDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)

        var time: String? = null
        var awayTeam: String? = null
        var homeTeam: String? = null

        arguments?.let { bundle ->
            awayTeam = bundle.getString("awayTeam")
            homeTeam = bundle.getString("homeTeam")
        }

        // Use the data to update UI or fetch more data
        fetchMatchData(homeTeam.toString(), awayTeam.toString())

        return binding.root
    }

    private fun fetchMatchData(homeTeam: String, awayTeam: String ) {
        lifecycleScope.launch {
            val database = Firebase.database
            val oldMatches = database.getReference("statistics")
            try {
                val oldMatchesSnapshot = withContext(Dispatchers.IO) {
                    oldMatches.get().await()
                }
                if (oldMatchesSnapshot.exists()) {
                    val oldMatchesData = oldMatchesSnapshot.getValue(object :
                        GenericTypeIndicator<ArrayList<MatchStatistics>>() {})
                    var thisMatch: MatchStatistics = MatchStatistics()
                    oldMatchesData?.forEach{match ->
//                        Log.d("ITM", "${match.homeTeam}: ${homeTeam}, ${match.awayTeam}: ${awayTeam}")
                        if (match.homeTeam.equals(homeTeam) && match.awayTeam.equals(awayTeam)){
                            thisMatch = match

                        }
                    }
                    binding.homeTeamGoal.text = thisMatch.homeTeamGoal.toString()
                    Log.d("ITM", "${binding.homeTeamGoal.text}")
                    // Update UI with this data
                    // Make sure to switch to the Main thread if updating UI
                }

            } catch (e: Exception) {
                Log.e("ITM", "Error fetching data: ${e.message}")
            }
        }
    }
}
