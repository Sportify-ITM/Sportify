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
        val item = itemList[position]
        holder.binding.homeTeam.text = item.homeTeam
        holder.binding.awayTeam.text = item.awayTeam
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
