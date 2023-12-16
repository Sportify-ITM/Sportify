package com.example.sportify

import MatchStatistics
import MatchTeamItem
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportify.adapter.MatchData
import com.example.sportify.adapter.MatchDetailAdapter
import com.example.sportify.databinding.FragmentMatchDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing


class MatchDetailFragment : Fragment() {

    private lateinit var binding: FragmentMatchDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerList.layoutManager = LinearLayoutManager(context)

        arguments?.let { bundle ->
            val awayTeam = bundle.getString("awayTeam") ?: ""
            val homeTeam = bundle.getString("homeTeam") ?: ""
            fetchMatchData(homeTeam, awayTeam)
        }
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
                            val newHomeTeam = changeNameFormat(homeTeam)
                            val newAwayTeam = changeNameFormat(awayTeam)
                            val homeTeamGoal = matchMap["${newHomeTeam}'s Goal"]
                            val awayTeamGoal = matchMap["${newAwayTeam}'s Goal"]

                            val homeTeamPossession = matchMap["${newHomeTeam}'s Ball Possession"].toString().removeSuffix("%").toFloatOrNull() ?: 0f
                            val awayTeamPossession = matchMap["${newAwayTeam}'s Ball Possession"].toString().removeSuffix("%").toFloatOrNull() ?: 0f

                            withContext(Dispatchers.Main) {
                                binding.homeTeamGoal.text = homeTeamGoal.toString()
                                binding.awayTeamGoal.text = awayTeamGoal.toString()
                                var matchDataList: ArrayList<MatchData> = ArrayList<MatchData>()
                                matchDataList.add(MatchData("Total Shots", matchMap["${newHomeTeam}'s Total Shots"].toString(), matchMap["${newAwayTeam}'s Total Shots"].toString()))
                                matchDataList.add(MatchData("Shots on Goal", matchMap["${newHomeTeam}'s Shots on Goal"].toString(), matchMap["${newAwayTeam}'s Shots on Goal"].toString()))
                                matchDataList.add(MatchData("expected_goals", matchMap["${newHomeTeam}'s expected_goals"].toString(), matchMap["${newAwayTeam}'s expected_goals"].toString()))
                                matchDataList.add(MatchData("Yellow Cards", matchMap["${newHomeTeam}'s Yellow Cards"].toString(), matchMap["${newAwayTeam}'s Yellow Cards"].toString()))
                                matchDataList.add(MatchData("Corner kicks", matchMap["${newHomeTeam}'s Corner Kicks"].toString(), matchMap["${newAwayTeam}'s Corner Kicks"].toString()))
                                matchDataList.add(MatchData("Fouls", matchMap["${newHomeTeam}'s Fouls"].toString(), matchMap["${newAwayTeam}'s Fouls"].toString()))
                                matchDataList.add(MatchData("Goalkeeper Saves", matchMap["${newHomeTeam}'s Goalkeeper Saves"].toString(), matchMap["${newAwayTeam}'s Goalkeeper Saves"].toString()))
                                binding.recyclerList.adapter = MatchDetailAdapter(matchDataList)
                                setImage(homeTeam, awayTeam)
                                setupPieChart(homeTeamPossession, awayTeamPossession)

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ITM", "Error fetching data: ${e.message}")
            }
        }
    }

    private fun setupPieChart(homePossession: Float, awayPossession: Float) {
        val entries = listOf(
            PieEntry(homePossession, "Home Team"),
            PieEntry(awayPossession, "Away Team")
        )

        val dataSet = PieDataSet(entries, "Ball Possession")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.blue)
        )
        dataSet.valueTextSize = 16f
        dataSet.valueTextColor = Color.WHITE
        dataSet.setDrawValues(true)

        val pieData = PieData(dataSet)
        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.isEnabled = true
            isRotationEnabled = true
            setEntryLabelColor(Color.WHITE)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
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
