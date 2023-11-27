package com.example.sportify

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
class CalenderFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    lateinit var binding: FragmentCalenderBinding
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentCalenderBinding.inflate(inflater, container, false)
//        lifecycleScope.launch {
//            val database = Firebase.database
//            val matches = database.getReference("matches")
//            try {
//                val matchesSnapshot = withContext(Dispatchers.IO) {
//                    matches.get().await()
//                }
//                if (matchesSnapshot.exists()) {
//                    val matchesData = matchesSnapshot.getValue(object : GenericTypeIndicator<ArrayList<MatchTeamItem>>() {})
//                    val eventDates = ArrayList<CalendarDay>()
//                    matchesData?.forEach { match ->
//                        val date = parseMatchDate(match.time)
//                        Log.d("Calendar", "Adding date: $date")
//                        date?.let {
//                            val day = CalendarDay.from(it)
//                            Log.d("Calendar", "Adding day: $day")
//                            eventDates.add(day)
//                        }
//                    }
//                    // To-Do, 나중에 선호하는 팀은 다른 색으로 칠할 수 있도록 조정.
//                    val eventDecorator = EventDecorator(eventDates)
//                    binding.calendarView.addDecorator(eventDecorator)
//                    binding.calendarView.setOnDateChangedListener { widget, date, selected ->
//                        var selectedDay = date.date
//                        selectedDay.let{
//                            val day = CalendarDay.from(it)
//                            if (eventDates.contains(day)){
//                                if (matchesData != null) {
//                                    updateCardDay(day, matchesData)
//                                }
////                                Log.d("ITM", "yes")
//                            }else{
////                                Log.d("ITM", "no")
//                            }
//                        }
//                    }
//                } else {
//                    Log.e("ITM", "No match data found")
//                }
//            } catch (e: Exception) {
//                Log.e("ITM", "Error fetching data: ${e.message}")
//            }
//        }
//        return binding.root
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_calender, container, false)
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            val database = Firebase.database
            val matches = database.getReference("matches")
            try {
                val matchesSnapshot = withContext(Dispatchers.IO) {
                    matches.get().await()
                }
                if (matchesSnapshot.exists()) {
                    val matchesData = matchesSnapshot.getValue(object : GenericTypeIndicator<ArrayList<MatchTeamItem>>() {})
                    val eventDates = ArrayList<CalendarDay>()
                    matchesData?.forEach { match ->
                        val date = parseMatchDate(match.time)
                        Log.d("Calendar", "Adding date: $date")
                        date?.let {
                            val day = CalendarDay.from(it)
                            Log.d("Calendar", "Adding day: $day")
                            eventDates.add(day)
                        }
                    }
                    // To-Do, 나중에 선호하는 팀은 다른 색으로 칠할 수 있도록 조정.
                    val eventDecorator = EventDecorator(eventDates)
                    binding.calendarView.addDecorator(eventDecorator)
                    //card update
                    binding.calendarView.setOnDateChangedListener { widget, date, selected ->
                        var selectedDay = date.date
                        selectedDay.let{
                            val day = CalendarDay.from(it)
                            if (eventDates.contains(day)){
                                if (matchesData != null) {
//                                    updateCardDay(day, matchesData)
                                    updateMatchRecyclerView(matchesData)
                                }
//                                Log.d("ITM", "yes")
                            }else{
//                                Log.d("ITM", "no")
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
        Log.d("ITM", "come here in function?")
        matchData?.let {
            val manager1 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter1 = CalenderCardAdpater(it)
            // Access the RecyclerView directly from the binding object
            binding.recyclerCardView.apply {
                adapter = adapter1
                layoutManager = manager1
            }
        }
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

//    fun changeVisibility(){
//        binding::class.java.declaredFields.forEach { field ->
//            field.isAccessible = true
//            val view = field.get(binding)
//            if (view is View) {
//                view.visibility = View.VISIBLE
//            }
//        }
//        if (binding.DefaultText.isVisible)
//            binding.DefaultText.visibility = View.INVISIBLE
//    }
//    fun updateCardDay(day: CalendarDay, match: ArrayList<MatchTeamItem>){
//        if (binding.DefaultText.isVisible)
//            binding.DefaultText.visibility = View.INVISIBLE
//        binding.matchDayText.text = day.toString()
//
//        changeVisibility()
//    }

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

