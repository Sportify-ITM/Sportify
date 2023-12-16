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
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportify.databinding.FragmentCalenderBinding
import com.google.firebase.Firebase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.database
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.example.sportify.CalenderCardAdapater
import okhttp3.internal.concurrent.formatDuration
import kotlin.collections.ArrayList

// ... other imports ...

class CalenderFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    lateinit var binding: FragmentCalenderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_calender, container, false)
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val database = Firebase.database
            val oldMatches = database.getReference("statistics")
            val matches = database.getReference("matches")
            try {
                var calendarMatch: ArrayList<MatchTeamItem> = ArrayList<MatchTeamItem>()
                val eventDates = ArrayList<CalendarDay>()
                val matchesSnapshot = withContext(Dispatchers.IO) {
                    matches.get().await()
                }
                val oldMatchesSnapshot = withContext(Dispatchers.IO) {
                    oldMatches.get().await()
                }

                if (matchesSnapshot.exists() and oldMatchesSnapshot.exists()) {
                    val matchesData = matchesSnapshot.getValue(object : GenericTypeIndicator<ArrayList<MatchTeamItem>>() {})
                    matchesData?.forEach { match ->
                        val date = parseMatchDate(match.time)
                        date?.let {
                            val day = CalendarDay.from(it)
                            eventDates.add(day)
                        }
                    }

                    val oldMatchesData = oldMatchesSnapshot.getValue(object : GenericTypeIndicator<ArrayList<MatchStatistics>>() {})
                    oldMatchesData?.let {
                        calendarMatch = parsingIntoMatch(it)
                    }
                    calendarMatch.forEach { match ->
                        val date = parseMatchDate(match.time)
                        date?.let {
                            val day = CalendarDay.from(it)
                            eventDates.add(day)
                        }
                    }

                    // To-Do, 나중에 선호하는 팀은 다른 색으로 칠할 수 있도록 조정.
                    val eventDecorator = EventDecorator(eventDates)
                    binding.calendarView.addDecorator(eventDecorator)
                    //card update
                    binding.calendarView.setOnDateChangedListener { widget, date, selected ->
                        Log.d("ITM", "another ")
                        var selectedDay = date.date
                        selectedDay.let{

                            val day = CalendarDay.from(it)
                            if (eventDates.contains(day)){
                                Log.d("DAY", "day: $day")
                                if (matchesData != null && calendarMatch != null ) {
                                    var combine = matchesData + calendarMatch
                                    updateMatchRecyclerView(filterMatchesByDay(combine, day))
                                }
                            }else{
                            }
                        }
                    }
                } else {
                    Log.e("ITM", "No match data found")
                }
            } catch (e: Exception) {
                Log.e("ITM", "Error fetching data: ${e.message}")
            }
        }
        return binding.root
    }



    private fun updateMatchRecyclerView(matchData: ArrayList<MatchTeamItem>?) {
        matchData?.let {

            val adapter_m = CalenderCardAdapater(it).apply {
                onItemClick = { matchItem ->
                    // Navigate to MatchDetailFragment
                    Log.d("ITM", "AAAA")
                    goToMatchDetailFragment(matchItem)
                }
            }
            val manager1 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter1 = CalenderCardAdapater(it)
            // Access the RecyclerView directly from the binding object
            binding.recyclerCardView.apply {
                this.adapter = adapter1
                layoutManager = manager1
            }
        }
    }

    private fun goToMatchDetailFragment(matchItem: MatchTeamItem) {
        // Create and navigate to MatchDetailFragment
        val matchDetailFragment = MatchDetailFragment().apply {
            arguments = Bundle().apply {
                // Pass data to the MatchDetailFragment
                // putString("key", matchItem.someProperty)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.calendarView, matchDetailFragment)
            .addToBackStack(null)
            .commit()
    }

    fun parseMatchDate(time: String): Date? {
        val year = "2023"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        return try {
            dateFormat.parse("$year-$time")
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    fun filterMatchesByDay(matchList: List<MatchTeamItem>, selectedDay: CalendarDay): ArrayList<MatchTeamItem> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Correctly format the selected day
        val selectedDateStr = String.format("%d-%02d-%02d", selectedDay.year, selectedDay.month + 1, selectedDay.day)
        val selectedDate = dateFormat.parse(selectedDateStr)

        // Filter the list and convert it to an ArrayList
        return ArrayList(matchList.filter {
            val matchDateStr = "2023-${it.time.split(" ")[0]}"
            val matchDate = dateFormat.parse(matchDateStr)
            matchDate == selectedDate
        })
    }
    fun parsingIntoMatch(matchList: ArrayList<MatchStatistics>): ArrayList<MatchTeamItem> {
        val calendarMatch = ArrayList<MatchTeamItem>() // Initialize as an empty ArrayList

        matchList.forEach { match ->
            val oldMatch = MatchTeamItem(
                awayTeam = match.awayTeam,
                homeTeam = match.homeTeam,
                time = match.time,
                status = "TIMED"
            )
            calendarMatch.add(oldMatch)
        }

        return calendarMatch
    }

    class EventDecorator(private val dates: Collection<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val shouldDecorate = dates.contains(day)
            return shouldDecorate
        }

        override fun decorate(view: DayViewFacade) {
            // Here you can customize how the day cell looks, e.g., set a custom background or dot
            view.addSpan(DotSpan(5f, Color.RED)) // Example: red dot decorator
        }
    }

}

