package com.example.sportify

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sportify.databinding.ActivityAddPhotoBinding
import com.example.sportify.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.SimpleTimeZone

class AddPhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null // 유저 정보
    var fireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission here
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        // Initialize
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // Set up the UI after permissions are granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setUpUI()
        }
    }

    private fun setUpUI() {
        // Inflate the layout
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("ITM", "${auth?.currentUser}")

        // Open the album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // Add image upload event
        binding.addPhotoBtnUpload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the selected image URI
                photoUri = data?.data

                // Display the selected image
                binding.addPhotoImage.setImageURI(photoUri)
            } else {
                // If no image is selected, finish the activity
                finish()
            }
        }
    }

    fun contentUpload() {
            // Create a file name based on the timestamp
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "IMAGE_${timeStamp}_.png"
            val storageRef = storage?.reference?.child("images")?.child(imageFileName)

            // 파일 업로드에 대한 콜백 메소드(방식 중에 하나)
            storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri -> //이미지 업로드 성공했으면,이미지 주소 받아오기
                    var contentDTO = ContentDTO()
                    //downloadUrl을 ContentDTO에 집어넣기
                    contentDTO.imageUrl = uri.toString()

                    //유저의 uid 집어넣기
                    contentDTO.uid = auth?.currentUser?.uid

                    //유저의 userId 집어넣기(이건 프로필 사진 용)
                    contentDTO.userId = auth?.currentUser?.email

                    //explain 집어넣기
                    contentDTO.explain = binding.addPhotoEdit.text.toString()

                    //시간 집어넣기
                    contentDTO.timeStamp = System.currentTimeMillis()

                    //싹다 집어넣은 contentDTO 인스턴스를 파이어스토어에 집어넣기
                    fireStore?.collection("images")?.document()?.set(contentDTO)
                        ?.addOnSuccessListener {
                            Log.d("ITM", "Data upload to Firestore successful.")
                            setResult(Activity.RESULT_OK)
                            finish() //창 닫기
                        }
                        ?.addOnFailureListener { e ->
                            Log.d("ITM", "Data upload to Firestore failed: ", e)
                        }
                }
            }
    }
}
