package com.example.sportify

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sportify.databinding.FragmentCommunityBinding
import com.example.sportify.databinding.ItemDetailBinding
import com.example.sportify.model.ContentDTO
import com.example.sportify.util.FcmPush
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment : Fragment() {
    lateinit var binding: FragmentCommunityBinding
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        binding.writeBtn.setOnClickListener { moveToAddPhotoActivity() }
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        val adapter1 = DetailViewRecyclerViewAdapter()
        val recyclerDetail = binding.communityfragmentRecyclerView
        recyclerDetail.apply {
            adapter = adapter1
            layoutManager = manager
            scrollToPosition(0) // Scroll to the top position
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
            firestore?.collection("profileImages")?.document(uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    if (documentSnapshot.data != null) {
                        var url = documentSnapshot?.data!!["image"]
                        Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop())
                            .into(viewHolder.detailviewitemProfileImage!!)
                    }
                }

            //좋아요 눌렀을 때 이벤트리스너
            viewHolder.detailviewitemFavoriteImageview.setOnClickListener{
                favoriteEvent(p1)
            }

            //좋아요 카운트와 하트 색칠/비움 이벤트
            if(contentDTOs!![p1].favorites.containsKey(uid)){
                viewHolder.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite)
            }else{
                viewHolder.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_favorite_border)
            }

            //유저 프로필이 클릭되었을 때
            viewHolder.detailviewitemProfileImage.setOnClickListener{
                var fragment = AccountFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[p1].uid)
                bundle.putString("userId",contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mainFrameLayout,fragment)?.commit()
            }
        }

        fun favoriteEvent(position : Int){
            //유저가 선택한 contentUid 값을 받아오기
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction{ transaction ->
                var uid = FirebaseAuth.getInstance().currentUser?.uid //현재 유저 id 받아오기
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java) //유저가 선택한 contentUid 값을 contentDTO 타입으로 캐스팅하기

                if(contentDTO!!.favorites.containsKey(uid)){ //좋아요가 이미 입력되어 있다면
                    // 좋아요 버튼이 눌리면
                    contentDTO.favoriteCount = contentDTO.favoriteCount-1 // TODO 이 부분 다름!!
                    contentDTO?.favorites?.remove(uid)// TODO 이 부분 다름!!
                }else{ //좋아요가 안 입력되어 있다면
                    contentDTO.favoriteCount = contentDTO.favoriteCount+1 // TODO 이 부분 다름!!
                    contentDTO.favorites[uid!!] = true// TODO 이 부분 다름!!
                    favoriteAlarm(contentDTOs[position].uid!!) // 좋아요 눌렀을때 알림
                }
                    transaction.set(tsDoc,contentDTO)//트랜잭션 서버로 돌려주기
            }
        }

        fun favoriteAlarm(destinationUid : String){
            var message = FirebaseAuth.getInstance()?.currentUser?.email + " " + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid,"Sportify",message)
        }

    }



    fun moveToAddPhotoActivity(){
        startActivity(Intent(requireActivity(), AddPhotoActivity::class.java))
    }
}