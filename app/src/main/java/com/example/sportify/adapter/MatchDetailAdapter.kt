package com.example.sportify.adapter

import MatchTeamItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportify.R

data class MatchData(
    val name: String,
    val home: String,
    val away: String,
    val homeValue: String,
    val awayValue: String
)

class MatchDetailAdapter(private val matchDataList: List<MatchData>) : RecyclerView.Adapter<MatchDetailAdapter.MatchDetailViewHolder>() {

    class MatchDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var listName: TextView = view.findViewById(R.id.listName)
        var home: TextView = view.findViewById(R.id.home)
        var away: TextView = view.findViewById(R.id.away)
        var homeValue: TextView = view.findViewById(R.id.homeValue)
        var awayValue: TextView = view.findViewById(R.id.awayValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_detail_items, parent, false)
        return MatchDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchDetailViewHolder, position: Int) {
        val matchData = matchDataList[position]
        holder.listName.text = matchData.name
        holder.home.text = matchData.home
        holder.away.text = matchData.away
        holder.homeValue.text = matchData.homeValue
        holder.awayValue.text = matchData.awayValue
    }

    override fun getItemCount() = matchDataList.size
}
