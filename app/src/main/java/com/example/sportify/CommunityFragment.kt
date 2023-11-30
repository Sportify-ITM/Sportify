package com.example.sportify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sportify.databinding.FragmentCommunityBinding
import com.example.sportify.databinding.ItemDetailBinding
import com.example.sportify.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment : Fragment() {
    lateinit var binding: FragmentCommunityBinding
    var firestore: FirebaseFirestore? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        binding.writeBtn.setOnClickListener { moveToAddPhotoActivity() }
        firestore = FirebaseFirestore.getInstance()
        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter1 = DetailViewRecyclerViewAdapter()
        val recyclerDetail = binding.communityfragmentRecyclerView
        recyclerDetail.apply {
            adapter = adapter1
            layoutManager = manager
        }
        return binding.root
    }
    //이번에는 어댑터를 이너클래스의 형태로 만듦
    inner class DetailViewRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()
        init{
            //파이어베이스에서 시간 순으로 데이터 가져오기 *addSnapshotListenr:데이터 수신되면 실행되는 코드
            firestore?.collection("images")?.orderBy("timeStamp")?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                //초기화
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java) //ContentDTO 형식으로 캐스팅하기
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                    Log.d("ITM","${contentDTOs}")
                }
                notifyDataSetChanged()//값이 새로고침되게 만듦
            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            val binding = ItemDetailBinding.inflate(LayoutInflater.from(p0.context), p0, false)
            return CustomViewHolder(binding)
        }


        inner class CustomViewHolder(val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root)


        override fun getItemCount(): Int {
            return contentDTOs.size
        }
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) { //p0 = 뷰홀더, p1 = 아이템 카운트
            var viewHolder = (p0 as CustomViewHolder).binding

            //유저 아이디 바인딩
            viewHolder.detailviewitemProfileTextview.text = contentDTOs!![p1].userId

            //이미지 바인딩
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewHolder.detailviewitemImageviewContent)

            //설명 글 바인딩
            viewHolder.detailviewitemExplainText.text = contentDTOs!![p1].explain

            //좋아요 수 바인딩
            viewHolder.detailviewitemFavoritecounterText.text = "Likes: "+contentDTOs!![p1].favoriteCount

            //프로필 이미지 바인딩
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewHolder.detailviewitemProfileImage)

        }


    }

    fun moveToAddPhotoActivity(){
        startActivity(Intent(requireActivity(), AddPhotoActivity::class.java))

    }
}