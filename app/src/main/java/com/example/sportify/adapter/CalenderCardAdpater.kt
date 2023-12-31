package com.example.sportify
import MatchTeamItem
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportify.databinding.CalendarCardviewBinding
import com.example.sportify.databinding.CardMatchesBinding

//recyclerHorizon을 위해 card_matches의 틀에 MatchTeamItem이라는 Data Class의 인스턴스를 홀딩해주는 어댑터
class CalenderCardAdapter(val itemList: List<MatchTeamItem>) : RecyclerView.Adapter<CalenderCardAdapter.CalenderViewHolder>() {

    // 리사이클러 뷰 아이템에 대한 클릭 리스너 파트
    var onItemClick: ((MatchTeamItem)  -> Unit)? = null

    fun setOnItemClickListener(listener: (MatchTeamItem)  -> Unit) {
        onItemClick = listener
    }

    //이 클래스는 RecyclerView의 각 항목에 대한 개별 뷰를 관리하는 뷰 홀더 클래스. 파라미터인 itemView는 개별 항목의 레이아웃

    inner class CalenderViewHolder(internal val binding: CalendarCardviewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                Log.d("ITM", "here?")
                onItemClick?.invoke(itemList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalenderViewHolder {
        val binding = CalendarCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalenderViewHolder(binding)
    }

    // 지정된 위치의 데이터를 ViewHolder 인스턴스에 바인딩하는 메소드
    override fun onBindViewHolder(holder: CalenderViewHolder, position: Int) {
        //itemList는 어댑터에서 파라미터로 받은 MatchItemList타입의 리스트
        val item = itemList[position]

        //바인딩 시작
        holder.binding.matchTime.text = item.time
        holder.binding.homeTeamText.text = item.homeTeam
        holder.binding.awayTeamText.text = item.awayTeam

        //드로어블에 있는 아이콘과 매칭하기 위해서 ic_팀이름을 homeIconName으로 선언하고 setImageResource로 이미지 삽입
        val homeIconName = "ic_${item.homeTeam.replace(" ", "_").lowercase()}"
        val homeResourceId = holder.itemView.resources.getIdentifier(homeIconName, "drawable", holder.itemView.context.packageName)
        holder.binding.homeTeamIcon.setImageResource(homeResourceId)
        val awayIconName = "ic_${item.awayTeam.replace(" ", "_").lowercase()}"
        val awayResourceId = holder.itemView.resources.getIdentifier(awayIconName, "drawable", holder.itemView.context.packageName)
        holder.binding.awayTeamIcon.setImageResource(awayResourceId)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}