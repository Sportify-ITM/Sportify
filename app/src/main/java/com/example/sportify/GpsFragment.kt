package com.example.sportify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sportify.databinding.FragmentGpsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class GpsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentGpsBinding
    private var mMap: GoogleMap? = null
    private var currentLat: Double = 0.0
    private var currentLon: Double = 0.0

    private val firestore = FirebaseFirestore.getInstance()
    private val locationsCollection = firestore.collection("locations")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGpsBinding.inflate(inflater, container, false)
        val rootView = binding.root

        currentLat = arguments?.getDouble("currentLat") ?: 0.0
        currentLon = arguments?.getDouble("currentLon") ?: 0.0

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setButton()

        return rootView
    }

    private fun setButton() {
        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(requireContext())
            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()

            mMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude!!, longitude!!),
                    16f
                )
            )
            setMarker()

            retrieveLocationsFromFirestore()
        }
    }

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

    private fun retrieveLocationsFromFirestore() {
        locationsCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")

                    if (latitude != null && longitude != null) {
                        val location = LatLng(latitude, longitude)
                        mMap?.addMarker(
                            MarkerOptions().position(location).title("Marker Location")
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.let {
            val currentLocation = LatLng(currentLat, currentLon)
            it.setMaxZoomPreference(20.0f)
            it.setMinZoomPreference(12.0f)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
            setMarker()
        }
    }

    private fun setMarker() {
        mMap?.let {
            it.clear()
            val markerOption = MarkerOptions()
            markerOption.position(it.cameraPosition.target)
            markerOption.title("Marker Location")
            val marker = it.addMarker(markerOption)

            it.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.position = it.cameraPosition.target
                }
            }

            it.setOnMarkerClickListener { clickedMarker ->
                val markerPosition = clickedMarker.position
                val latitude = markerPosition.latitude
                val longitude = markerPosition.longitude

                Toast.makeText(
                    requireContext(),
                    "Latitude: $latitude, Longitude: $longitude",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }
        }
    }
}
