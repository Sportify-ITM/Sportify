package com.example.sportify

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat

// GPS나 Network의 위치를 사용해 위도와 경도를 가져오는 게 주목적이다.
class LocationProvider (val context : Context) { // context는 위치를 가져올 때 필요함.
    // [Property 정의]
    // location: 위치(위도와 경도)를 잡는 객체
    // locationManager: 시스템 위치서비스에 접근을 제공하는 class
    private var location : Location? = null
    private var locationManager : LocationManager? = null

    // 초기화 함수
    init {
        getLocation()
    }

    private fun getLocation() : Location? {
        try {
            // LocationProvider는  context가 없기 때문에 context를 인수로 받아줘서 거기서 getSystemService를 불러준다.
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            var gpsLocation : Location? = null
            var networkLocation : Location? = null

            // [GPS or Network가 활성화 되었는지 확인]
            // GPS_PROVIDER를 사용할 수 있는지를 Boolean값으로 반환.
            // !!는 locationManager가 null아면 catch문으로 빠짐.
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // GPS & Network 둘 다 사용불가능하면,
            if (!isGPSEnabled && !isNetworkEnabled) {
                return null // null 반환
            } else {
                // else 안에서는 Network를 통한 위치파악이 가능한 경우에는 위치를 가져오고,
                // GPS가 가능한 경우 GPS에서 위치를 가져온다.
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) { // Permission이 둘 다 check 되어 있지 않으면, null 반환
                    return null
                }
                if (isNetworkEnabled) {
                    networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
                if(isGPSEnabled) {
                    gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                if (gpsLocation != null && networkLocation != null) {
                    if (gpsLocation.accuracy > networkLocation.accuracy) {
                        location = gpsLocation
                    } else{
                        location = networkLocation

                    }
                } else {
                    if (gpsLocation != null) {
                        location = gpsLocation
                    }
                    if (networkLocation != null) {
                        location = networkLocation
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return location
    }
    // location 기반으로 위도, 경도를 가져오는 함수
    fun getLocationLatitude() : Double? {
        return location?.latitude
    }
    fun getLocationLongitude() : Double? {
        return location?.longitude
    }
}