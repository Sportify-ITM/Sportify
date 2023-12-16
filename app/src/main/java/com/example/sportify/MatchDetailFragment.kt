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

    private fun fetchMatchData(homeTeam: String, awayTeam: String) {
        lifecycleScope.launch {
            val database = Firebase.database
            val oldMatchesRef = database.getReference("statistics")
            try {
                val oldMatchesSnapshot = oldMatchesRef.get().await()
                if (oldMatchesSnapshot.exists()) {
                    val matchesList = oldMatchesSnapshot.children.toList()
                    for (matchSnapshot in matchesList) {
                        val matchMap = matchSnapshot.value as? Map<String, Any> ?: continue
                        val matchHomeTeam = matchMap["homeTeam"] as? String
                        val matchAwayTeam = matchMap["awayTeam"] as? String

                        if (matchHomeTeam == homeTeam && matchAwayTeam == awayTeam) {
                            // Extract other fields as needed
                            Log.d("ITM", "m: ${matchMap}")
                            setImage(homeTeam, awayTeam)
                            val newHomeTeam = changeNameFormat(homeTeam)
                            val newAwayTeam = changeNameFormat(awayTeam)
                            Log.d("ITM", "after: ${newHomeTeam}, before: ${homeTeam}")
                            Log.d("ITM", "after: ${newAwayTeam}, before: ${awayTeam}")
                            val homeTeamGoal = matchMap["${newHomeTeam}'s Goal"]
                            val awayTeamGoal = matchMap["${newAwayTeam}'s Goal"]
                            Log.d("ITM", "${homeTeamGoal}")
                            Log.d("ITM", "${awayTeamGoal}")
                            // Update UI or process data
                            withContext(Dispatchers.Main) {
                                binding.homeTeamGoal.text = homeTeamGoal.toString()
                                binding.awayTeamGoal.text = awayTeamGoal.toString()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ITM", "Error fetching data: ${e.message}")
            }
        }
    }

    fun changeNameFormat(teamName: String): String? {
        var newName: String? = null

        if (teamName == "Nottingham")
            newName = "Nottingham Forest"
        else if (teamName == "Luton Town")
            newName = "Luton"
        else if (teamName == "Man City")
            newName = "Manchester City"
        else if (teamName == "Man united")
            newName = "Manchester United"
        else if (teamName == "Sheffield")
            newName = "Sheffield Utd"
        else if (teamName == "WolverHampton")
            newName = "Wolves"
        else
            newName = teamName

        return newName
    }

//    private fun fetchMatchData(homeTeam: String, awayTeam: String ) {
//        lifecycleScope.launch {
//            val database = Firebase.database
//            val oldMatches = database.getReference("statistics")
//            try {
//                val oldMatchesSnapshot = withContext(Dispatchers.IO) {
//                    oldMatches.get().await()
//                }
//                if (oldMatchesSnapshot.exists()) {
//                    Log.d("ITM", "Raw Data: ${oldMatchesSnapshot}") // Log the raw data
//                    val oldMatchesData = oldMatchesSnapshot.getValue(object : GenericTypeIndicator<ArrayList<MatchStatistics>>() {})
//                    var thisMatch = MatchStatistics()
//
//                    oldMatchesData?.forEach{match ->
//                        if (match.homeTeam.equals(homeTeam) && match.awayTeam.equals(awayTeam)){
//                            Log.d("ITM", "${match.homeTeam}: ${homeTeam}, ${match.awayTeam}: ${awayTeam}")
//                            thisMatch = match
//                        }
//                    }
//                    binding.homeTeamGoal.text = thisMatch.homeTeamGoal.toString()
//                    binding.awayTeamGoal.text = thisMatch.awayTeamGoal.toString()
//                    setImage(homeTeam, awayTeam)
//                    Log.d("ITM", "${binding.homeTeamGoal.text}")
//                    // Update UI with this data
//                    // Make sure to switch to the Main thread if updating UI
//                }
//
//            } catch (e: Exception) {
//                Log.e("ITM", "Error fetching data: ${e.message}")
//            }
//        }
//    }

    private fun setImage(homeTeam: String, awayTeam: String) {
        val context = requireContext()
        val homeIconName = "ic_${homeTeam.replace(" ", "_").lowercase()}"
        val homeResourceId = context.resources.getIdentifier(homeIconName, "drawable", context.packageName)
        binding.homeImage.setImageResource(homeResourceId)

        val awayIconName = "ic_${awayTeam.replace(" ", "_").lowercase()}"
        val awayResourceId = context.resources.getIdentifier(awayIconName, "drawable", context.packageName)
        binding.awayImage.setImageResource(awayResourceId)
    }
}
