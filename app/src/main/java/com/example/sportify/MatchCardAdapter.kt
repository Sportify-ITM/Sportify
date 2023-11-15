package com.example.sportify
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportify.databinding.CardMatchesBinding

//recyclerHorizon을 위해 card_matches의 틀에 MatchTeamItem이라는 Data Class의 인스턴스를 홀딩해주는 어댑터
class MatchCardAdapter(val itemList: List<MatchTeamItem>) : RecyclerView.Adapter<MatchCardAdapter.ViewHolder>() {

    // 리사이클러 뷰 아이템에 대한 클릭 리스너 파트
    private var onItemClickListener: ((position: Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        onItemClickListener = listener
    }

    //이 클래스는 RecyclerView의 각 항목에 대한 개별 뷰를 관리하는 뷰 홀더 클래스. 파라미터인 itemView는 개별 항목의 레이아웃
    inner class ViewHolder(internal val binding: CardMatchesBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }

    //새 ViewHolder 인스턴스를 만드는 메소드. LayoutInflater를 사용해서 CardMatchesBinding을 인플레이트함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardMatchesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // 지정된 위치의 데이터를 ViewHolder 인스턴스에 바인딩하는 메소드
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //itemList는 어댑터에서 파라미터로 받은 MatchItemList타입의 리스트
        val item = itemList[position]

        //바인딩 시작
        holder.binding.matchTime.text = item.matchTime
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
