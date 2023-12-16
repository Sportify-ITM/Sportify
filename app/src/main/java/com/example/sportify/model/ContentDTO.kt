package com.example.sportify.model

data class ContentDTO(
    var explain: String? = null, // 콘텐츠 내용
    var imageUrl: String? = null,
    var uid: String? = null, // 어느 유저가 올렸는지
    var userId: String? = null, // 올린 유저의 프로필 이미지
    var timeStamp: Long? = null,
    var favoriteCount: Int = 0, // 좋아요 수
    var favorites: MutableMap<String,Boolean> = HashMap()
    ){
    data class Comment(     // 댓글에 대한 데이터 클래스
        var uid : String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timeStamp: Long? = null
        )
}