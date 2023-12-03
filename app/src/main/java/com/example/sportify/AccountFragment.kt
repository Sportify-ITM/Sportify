package com.example.sportify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sportify.databinding.FragmentAccountBinding
import com.example.sportify.model.ContentDTO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountFragment : Fragment() {
    lateinit var binding: FragmentAccountBinding
    var firestore: FirebaseFirestore? = null
    var fragmentView: View? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        uid = arguments?.getString("destinationUid")
        Log.d("ITMM","$uid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        if(uid == currentUserUid){
            //내 페이지
            binding?.accountBtnFollowSignout?.text = "Edit"
        }else{
            //다른 유저 페이지
            binding?.accountBtnFollowSignout?.text = "Follow"
            var mainActivity = activity as MainActivity
            mainActivity.findViewById<TextView>(R.id.toolbar_username).apply{
                text = arguments?.getString("userId")
            }
            mainActivity.findViewById<ImageView>(R.id.toolbar_btn_back).apply{
                setOnClickListener { mainActivity.findViewById<BottomNavigationView>(R.id.navigationView).selectedItemId = R.id.home }
            }
            mainActivity.findViewById<TextView>(R.id.toolbar_username).visibility = View.VISIBLE
            mainActivity.findViewById<ImageView>(R.id.toolbar_btn_back).visibility = View.VISIBLE
        }



        val adapter1 = AccountFragmentRecyclerViewAdapter()
        val manager = GridLayoutManager(requireActivity()!!,3)
        val recyclerAccount = binding.accountRecyclerview
        recyclerAccount.apply {
            adapter = adapter1
            layoutManager = manager
        }
        return binding.root
    }

    inner class AccountFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs:ArrayList<ContentDTO> = arrayListOf()
        init {
            //uid가 내 것일 때만 검색되도록 쿼리 구성
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener{querySnapshot,firebaseFirestoreException ->
                // 안정성을 위해, 쿼리 스냅샷 결과가 null이면 종료하게끔 설정
                if(querySnapshot == null) return@addSnapshotListener
                //데이터 받기
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                binding?.accountTvPostCount?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            // 폭의 1/3 가져오기
            var width = resources.displayMetrics.widthPixels  / 3
            // 정사각형의 이미지 뷰 만들기
            var imageView = ImageView(p0.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageView)

        }

        // 넘어온 이미지 뷰를 리사이클러 뷰의 뷰 홀더로 전달
        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            // 이미지 뷰를 커스텀 뷰 홀더로 캐스팅
            var imageView = (p0 as CustomViewHolder).imageView
            Glide.with(p0.itemView.context)  // Glide를 아이템 뷰의 컨텍스트로 초기화
                .load(contentDTOs[p1].imageUrl)  // 지정된 URL(contentDTOs[p1].imageUrl)에서 이미지를 로드
                .apply(RequestOptions().centerCrop())  // 이미지 로딩 옵션을 적용, center crop
                .into(imageView)  // 이미지를 지정된 ImageView(imageView)에 로드

        }

    }



}