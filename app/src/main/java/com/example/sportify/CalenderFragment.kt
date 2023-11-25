package com.example.sportify

import MatchTeamItem
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                    binding.calendarView.setOnDateChangedListener { widget, date, selected ->
                        var selectedDay = date.date
                        selectedDay.let{
                            val day = CalendarDay.from(it)
                            if (eventDates.contains(day)){

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

//    fun updateCardDay(day: CalendarDay){
//        binding.eventTextView.text
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

