package com.example.sportify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sportify.databinding.FragmentCalenderBinding


class CalendarFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }
    lateinit var binding: FragmentCalenderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentCalenderBinding.inflate(inflater, container, false)

//        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
//            val selectedDate = "$dayOfMonth/${month + 1}/$year"
//            // 여기서 selectedDate를 사용하여 일정을 검색
//            updateCardViewWithEvent(selectedDate)
//        }
        binding.calendarView.setOnDateChangeListener{view, year, month, dayOfMonth ->
//            val selectedDate = "$dayOfMonth/${month + 1}/$year"

//            Log.d("ITM", "$selectedDate")

        }

        return binding.root
    }

    fun updateCardViewWithEvent(date: String) {
        val eventDetails = getEventDetailsForDate(date) // 이 함수는 해당 날짜의 일정을 반환해야 함
        binding.eventTextView.text = eventDetails
        binding.eventCardView.visibility = View.VISIBLE // 카드뷰를 보이게 설정
    }

    fun getEventDetailsForDate(date: String): String {
        // 여기에 일정 데이터를 검색하는 로직 구현
        // 예시: "2023-10-16: 축구 경기"
        return "2023-10-16: 축구 경기"
    }
}