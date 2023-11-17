package com.example.sportify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportify.databinding.TableTeamsBinding
class TeamTableAdapter(val itemList: List<TeamTable>):  RecyclerView.Adapter<TeamTableAdapter.ViewHolder>(){

    //이 클래스는 RecyclerView의 각 항목에 대한 개별 뷰를 관리하는 뷰 홀더 클래스. 파라미터인 itemView는 개별 항목의 레이아웃
    inner class ViewHolder(internal val binding: TableTeamsBinding) : RecyclerView.ViewHolder(binding.root) {

        // 리사이클러 뷰 아이템에 대한 클릭 리스너 파트
        private var onItemClickListener: ((position: Int) -> Unit)? = null
        fun setOnItemClickListener(listener: (position: Int) -> Unit) {
            onItemClickListener = listener
        }
        init {
            binding.root.setOnClickListener {
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }
    //새 ViewHolder 인스턴스를 만드는 메소드. LayoutInflater를 사용해서 CardMatchesBinding을 인플레이트함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TableTeamsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tableTeamRank.text = item.rank.toString()
        holder.binding.tableTeamName.text = item.teamName
        val tableTeamIconName = "ic_${item.teamName.replace(" ", "_").lowercase()}"
        val tableTeamResourceId = holder.itemView.resources.getIdentifier(tableTeamIconName, "drawable", holder.itemView.context.packageName)
        holder.binding.tableTeamIcon.setImageResource(tableTeamResourceId)
        holder.binding.tableTeamMatches.text = "Matches: "+item.matchesPlayed.toString()
        holder.binding.tableTeamWin.text = "Win: "+item.wins.toString()
        holder.binding.tableTeamDraw.text = "Draw: "+item.draws.toString()
        holder.binding.tableTeamLose.text = "Loss: "+item.losses.toString()
        holder.binding.tableTeamPoint.text = "Points: "+item.point.toString()

    }
    override fun getItemCount(): Int {
        return itemList.size
    }
}
