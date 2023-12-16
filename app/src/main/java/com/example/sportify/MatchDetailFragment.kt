package com.example.sportify

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

data class Statistics(val stat1: Int,
                      val stat2: Int,
                      val stat3: Int,
                      val stat4: Int,
                      val stat5: Int,
                      val stat6: Int,
                      val stat7: Int,
                      val stat8: Int,
                      val stat9: Int,
                      val stat10: Int,
                      val stat11: Int,
                      val stat12: Int,
                      val stat13: Int,
                      val stat14: Int,
                      val stat15: Int,
                      val stat16: Int,
                      val stat17: Int)
class MatchDetailFragment : Fragment() {
    val database = Firebase.database
    val statistics = database.getReference("statistics")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 여기부터 파이어베이스에서 EPL 데이터 불러오는 파트
        val view = inflater.inflate(R.layout.fragment_match_detail,container, false)

        statistics.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val test = snapshot.child("statistics")
                for (data in test.children) {
                    Log.d("snap", data.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed
            }
        })
        return view
    }
}