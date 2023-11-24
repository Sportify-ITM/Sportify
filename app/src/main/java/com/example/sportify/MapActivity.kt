package com.example.sportify

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.sportify.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityMapBinding

    private var mMap: GoogleMap? = null
    var currentLat: Double = 0.0
    var currentLon: Double = 0.0

    private val firestore = FirebaseFirestore.getInstance() // Firebase
    private val locationsCollection = firestore.collection("locations") // Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =    ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLat = intent.getDoubleExtra("currentLat", 0.0)
        currentLon = intent.getDoubleExtra("currentLon", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setButton()
    }

    private fun setButton() {
        binding.btnCheckHere.setOnClickListener() {
            mMap?.let{
                val intent = Intent()
                val location = it.cameraPosition.target // Firebase
                intent.putExtra("latitude", it.cameraPosition.target.latitude)
                intent.putExtra("longitude", it.cameraPosition.target.longitude)

                // Save the location to Firestore
                saveLocationToFirestore(location.latitude, location.longitude) // Firebase
                saveLocationToFirestore(37.52990242301772, 126.96466236081207) // 용산역
                saveLocationToFirestore(37.4670, 126.9491) // SNU
                saveLocationToFirestore(37.5894, 127.0325) // Korea University

                 setResult(Activity.RESULT_OK, intent)
                 finish()
            }
        }
        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(this@MapActivity)
            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()

            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 16f))
            setMarker()

            // Retrieve and display locations from Firestore
            retrieveLocationsFromFirestore()
        }
    }

    // Function to save location to Firestore
    private fun saveLocationToFirestore(latitude: Double, longitude: Double) {
        val locationData = hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )

        locationsCollection.add(locationData)
            .addOnSuccessListener { documentReference ->
                // Handle success if needed
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
            }
    }

    // Function to retrieve locations from Firestore and display markers on the map
    private fun retrieveLocationsFromFirestore() {
        locationsCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")

                    if (latitude != null && longitude != null) {
                        val location = LatLng(latitude, longitude)
                        mMap?.addMarker(MarkerOptions().position(location).title("Marker Location"))
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Handle map ready callback
        mMap = googleMap

        mMap?.let{
            val currentLocation = LatLng(currentLat, currentLon)
            it.setMaxZoomPreference(20.0f)
            it.setMinZoomPreference(12.0f)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
            setMarker()
        }
    }
    private fun setMarker() {
        mMap?.let{
            it.clear()
            val markerOption = MarkerOptions()
            markerOption.position(it.cameraPosition.target)
            markerOption.title("Marker Location")
            val marker = it.addMarker(markerOption)

            it.setOnCameraMoveListener {
                marker?.let{ marker ->
                    marker.position = it.cameraPosition.target
                }
            }

            it.setOnMarkerClickListener { clickedMarker ->
                // 마커를 클릭했을 때의 동작
                val markerPosition = clickedMarker.position
                val latitude = markerPosition.latitude
                val longitude = markerPosition.longitude

                // 토스트 메시지로 위도와 경도 보여주기
                Toast.makeText(this@MapActivity, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()

                // true를 반환하면 마커의 기본 동작을 수행하지 않습니다.
                true
            }


        }
    }
}