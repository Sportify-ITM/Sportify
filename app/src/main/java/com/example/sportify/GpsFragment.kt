package com.example.sportify

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
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

        val locationProvider = LocationProvider(requireContext())
        val latitude = locationProvider.getLocationLatitude()
        val longitude = locationProvider.getLocationLongitude()

        val currentUser = FirebaseManager.authInstance.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            // 이제 userId를 사용하여 필요한 작업 수행
            Log.d("UserInfo", "User ID: $userId")
        } else {
            Log.d("UserInfo", "No user is currently logged in.")
        }

        val logo = firestore.collection("logos").document(currentUser!!.uid)

        logo.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // The document exists, check if it has the "logo" field
                    val logoValue = documentSnapshot.getLong("logo")

                    // Check if the "logo" field exists and has a non-null value
                    if (logoValue != null) {
                        // Now you have the logo value (e.g., 1234)
                        Log.d("logo", "Logo Value: $logoValue")
                        updateLocationToFirebase(currentUser!!.uid, latitude, longitude, logoValue)
                    } else {
                        // Handle the case where "logo" field is missing or null
                        Log.e("logo", "Invalid or missing 'logo' field")
                    }
                } else {
                    // Handle the case where the document doesn't exist
                    Log.e("logo", "Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                // Handle failures
                Log.e("logo", "Error getting document: $e")
            }



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

            // Show 1km radius circle
            showOneKmCircle(LatLng(latitude!!, longitude!!))

            retrieveLocationsFromFirestore()
        }
    }

    private fun showOneKmCircle(center: LatLng) {
        // Add circle to represent 1km radius
        val circleOptions = CircleOptions()
            .center(center)
            .radius(1000.0) // Radius in meters
            .strokeColor(Color.BLUE) // Circle stroke color
            .fillColor(Color.parseColor("#500084d3")) // Circle fill color with transparency

        mMap?.addCircle(circleOptions)
    }

    private fun retrieveLocationsFromFirestore() {
        // new
        val locationProvider = LocationProvider(requireContext())
        val current_latitude = locationProvider.getLocationLatitude()
        val current_longitude = locationProvider.getLocationLongitude()
        Log.d("ITM: Latitude",current_latitude.toString())
        Log.d("ITM: Longitude",current_longitude.toString())

        locationsCollection.get()
            .addOnSuccessListener { documents ->
                val logoList: MutableList<String> = mutableListOf()
                for (document in documents) {
                    val latitude = document.getDouble("latitude")
                    val longitude = document.getDouble("longitude")
                    val logo = document.getString("logo")
                    val logoId = document.getLong("logoId")?.toInt()

                    if (latitude != null && longitude != null && logo != null && logoId != null) {
                        val location = LatLng(latitude, longitude)

                        // new
                        val distance = calculateDistance(
                            current_latitude!!,
                            current_longitude!!,
                            location.latitude,
                            location.longitude
                        )

                        // new
                        // Check if the distance is within 1km
                        if (distance <= 1000) {
                            val currentUser = FirebaseManager.authInstance.currentUser
                            val userId = currentUser!!.uid
                            val userDocument = firestore.collection("locations").document(userId)
                            // Check if the marker is for the user's location
                            if (userDocument.id != document.id) {
                                logoList.add(logo)
                                // Load the original logo bitmap
                                val originalLogoBitmap = BitmapFactory.decodeResource(resources, logoId!!)

                                // Calculate the desired width and height (e.g., 100x100 pixels)
                                val desiredWidth = 100
                                val desiredHeight = 100

                                // Resize the bitmap
                                val resizedLogoBitmap = Bitmap.createScaledBitmap(originalLogoBitmap, desiredWidth, desiredHeight, false)

                                // Add the marker with the resized logo
                                mMap?.addMarker(
                                    MarkerOptions()
                                        .position(location)
                                        .title("Marker Location")
                                        .icon(BitmapDescriptorFactory.fromBitmap(resizedLogoBitmap))
                                )
                            }

                        }
                    }
                }

                // Count occurrences of each logo
                val logoCountMap = logoList.groupingBy { it }.eachCount()

                // Create the message for Toast
                val toastMessage = buildString {
                    append("Within 1km radius of your location, ")
                    logoCountMap.forEach { (logo, count) ->
                        append("$logo: $count, ")
                    }
                    // Remove the trailing comma and space
                    if (length > 2) {
                        delete(length - 2, length)
                    }
                }

                if (logoList.size > 0) {
                    // Display a Toast message with the counts
                    Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "There is no friend within 1km radius of your location.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
            }
        // Add marker click listener to mMap
        mMap?.setOnMarkerClickListener { marker ->
            // Get the latitude and longitude of the clicked marker
            val clickedLatitude = marker.position.latitude
            val clickedLongitude = marker.position.longitude

            // Retrieve all documents from Firestore based on the clicked location
            locationsCollection
                .whereEqualTo("latitude", clickedLatitude)
                .whereEqualTo("longitude", clickedLongitude)
                .get()
                .addOnSuccessListener { documents ->
                    // Iterate through all documents at the clicked location
                    for (document in documents) {
                        val currentUser = FirebaseManager.authInstance.currentUser
                        val userId = currentUser!!.uid
                        val userDocument = firestore.collection("locations").document(userId)
                        if (userDocument.id != document.id) {
                            // Get the logo field value from each document
                            val clickedLogo = document.getString("logo")
                            // Display Toast with the logo information
                            Toast.makeText(requireContext(), "Clicked Marker Logo: $clickedLogo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure if needed
                }

            // Return true to consume the event (if false, the default behavior will occur)
            true
        }
    }

    // GpsFragment 클래스 내에 추가할 함수
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val locationProvider = LocationProvider(requireContext())
        val latitude = locationProvider.getLocationLatitude()
        val longitude = locationProvider.getLocationLongitude()

        mMap?.let {
            val currentLocation = LatLng(latitude!!, longitude!!)
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

                true
            }
        }
    }

    // Mapping of logo values to team identifiers
    val logoToTeamIdMap = mapOf(
        2131165348 to "arsenal",
        2131165349 to "aston_villa",
        2131165350 to "bournemouth",
        2131165352 to "brighton",
        2131165351 to "brentford",
        2131165353 to "burnley",
        2131165355 to "chelsea",
        2131165360 to "crystal_palace",
        2131165362 to "everton",
        2131165365 to "fulham",
        2131165371 to "liverpool",
        2131165372 to "luton_town",
        2131165376 to "man_city",
        2131165377 to "man_united",
        2131165382 to "newcastle",
        2131165383 to "nottingham",
        2131165386 to "sheffield",
        2131165387 to "tottenham",
        2131165388 to "west_ham",
        2131165389 to "wolverhampton"
    )

    // Function to get team identifier based on logo value
    fun getTeamIdFromLogo(logoValue: Long): String? {
        return logoToTeamIdMap[logoValue.toInt()]
    }

    private fun updateLocationToFirebase(userId: String, latitude: Double?, longitude: Double?, logoId: Long?){

        if (logoId != null) {
            val locationsMap = hashMapOf(
                "logo" to getTeamIdFromLogo(logoId ?: 0),
                "logoId" to logoId,
                "latitude" to latitude,
                "longitude" to longitude,
            )
            firestore.collection("locations").document(userId)
                .set(locationsMap)
                .addOnSuccessListener {
                    Log.d("locations", "Locations updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("locations", "Error updating locations: $e")
                }
        } else {
            Log.e("locations", "Invalid logoId identifier")
        }
    }
}