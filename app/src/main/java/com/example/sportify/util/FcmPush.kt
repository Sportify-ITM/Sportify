package com.example.sportify.util

import com.example.sportify.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class FcmPush {

    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val url = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "AAAAB68I_a0:APA91bEv0t7gSQ4wEb-racR6D2lfS6oaWHcBDw9ZhcdSdQdP-_jBSYj39ayVH8ap96NXNLFEVIuUgRw8HjWhLiDD1uKOHDF0SCG6YbE1RE0seOHrcZFi16iPDQ-uC1Szeuoy4H0zQ__C"
    private val gson: Gson = Gson()
    private val okHttpClient: OkHttpClient = OkHttpClient()

    companion object {
        val instance = FcmPush()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.get("pushToken").toString()

                    val pushDTO = PushDTO()
                    pushDTO.to = token
                    pushDTO.notification.title = title
                    pushDTO.notification.body = message

                    val body = gson.toJson(pushDTO).toRequestBody(JSON)
                    val request = Request.Builder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "key=$serverKey")
                        .url(url)
                        .post(body)
                        .build()

                    okHttpClient.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            // Handle failure
                        }

                        override fun onResponse(call: Call, response: Response) {
                            println(response.body?.string())
                        }
                    })
                }
            }
    }
}
