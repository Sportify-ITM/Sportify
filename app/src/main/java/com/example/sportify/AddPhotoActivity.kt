package com.example.sportify

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sportify.databinding.ActivityAddPhotoBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.SimpleTimeZone

class AddPhotoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAddPhotoBinding

    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission here
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        // Initialize storage
        storage = FirebaseStorage.getInstance()

        // Set up the UI after permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            setUpUI()
        }
    }

    private fun setUpUI() {
        // Inflate the layout
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        // Check if photoUri is not null before proceeding with the upload
        photoUri?.let { uri ->
            // Create a file name based on the timestamp
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "IMAGE_${timeStamp}_.png"
            val storageRef = storage?.reference?.child("images")?.child(imageFileName)

            // Upload the file
            storageRef?.putFile(uri)?.addOnSuccessListener {
                Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
            }?.addOnFailureListener {
                Toast.makeText(this, getString(R.string.upload_fail), Toast.LENGTH_LONG).show()
            }
        }
    }
}
