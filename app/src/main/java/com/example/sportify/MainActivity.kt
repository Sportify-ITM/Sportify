package com.example.sportify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sportify.databinding.ActivityMainBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//hi from kangmin
class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    // PERMISSINS_REQUEST_CODE: PERMISSIONS의 ID값이라고 생각하면 편함.
    // Response를 받았을 때 100이 온다면 우리가 요청한 2가지 Request에 대한 Response가 왔구나 라는 걸 알 수 있음.
    private val PERMISSIONS_REQUEST_CODE = 100
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var getGPSPermissionLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAllPermissions()
    }

    private fun checkAllPermissions() {
        // GPS가 켜져있는지 확인
        if(!isLocationServicesAvailable()) { // GPS가 꺼져있다면
            // GPS가 꺼졌을 때 User에게 GPS를 켜주세요. 하는 함수
            showDialogForLocationServiceSetting()
        } else { // GPS가 켜져있다면
            isRunTimePermissionsGranted()
        }
    }

    private fun isLocationServicesAvailable() : Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager // Type Casting
        // Location Provider는 GPS나 Network를 Provider로 설정할 수 있다.
        // GPS_PROVIDER의 경우, 위성신호를 수신해 위치를 구함.
        // NETWORK_PROVIDER의 경우, Wifi-Network나 기지국으로부터 위치를 구함.
        // 따라서, 둘 중 하나라도 enabled 되어 있다면 true로 반환.
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun isRunTimePermissionsGranted() {
        // Permission이 있는지 없는지 확인
        // COARSE_LOCATION: 대략적인 위치
        // FINE_LOCATION: 좀 더 자세한 위치
        // GPS를 사용하냐, Network를 사용하냐에 따라 약간의 차이가 있지만, GPS든 Network든 사용하려면 두 가지 Permissions를 추가.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

        // 둘 중 하나라도 권한이 없다면
        if(hasFineLocationPermission!= PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // 내 기기 위치에 엑세스 하도록 허용하시겠습니까? 라는 Dialogue 팝업.
            // REQUIRED_PERMISSIONS: Permission을 요청할 List
            // PERMISSIONS_REQUEST_CODE: 요청할 코드(Request를 보내는 사람이 누군지 이름을 명시.)
            ActivityCompat.requestPermissions(this@MainActivity,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE)
        }
    }

    // ActivityCompat.requestPermissions의 requestPermissions를 처리하기 위해 override
    // 그 결과 값은 Activity에서 onRequestPermissionsResult 라는 함수를 Override 함으로써 구현.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 아래의 if 문을 통해, 만약 requestCode와 PERMISSIONS_REQUEST_CODE (Request를 보내는 사람)가 같고 Size도 같다면
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true

            // 아래의 for문을 통해 모든 권한이 허용 되었는지 확인
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break;
                }
            }

            // 권한이 모두 허용돼서 checkResult가 True라면, 위치값을 가져올 수 있다.
            if (checkResult) {
                // 위치값을 가져올 수 있음
            } else {
                // 권한이 모두 허용되지 않았다면, Toast Message 후 Activity 종료
                Toast.makeText(this@MainActivity, "Permission denied. Please accept permissions.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
