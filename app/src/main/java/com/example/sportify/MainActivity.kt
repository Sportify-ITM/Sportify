package com.example.sportify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sportify.databinding.ActivityMainBinding
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
        // 즉, 내가 보낸 Request가 맞다면
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
    // Activity result launcher 정의
    private fun showDialogForLocationServiceSetting() {
        // Activity result Launcher 설정
        // Launcher를 이용하면, 결과를 반환해야 하는 intent를 설정할 수 있다.
        // 기존에 A activity에서 B activity로 넘어갈 때 StartActivity를 사용하면서 intent를 인수로 넘겨줬다.
        // 그 때는 결과값을 반환하지는 않았다. 그냥 A에서 B로 넘어가고 끝이였다.
        // 이번에는 Launcher를 통해서 A에서 B로 넘어간 다음에 B에서 A로 데이터를 보내줘야하는 상황이다.
        // 그때 사용하는 것이 ActivityResultLauncher이다.
        // registerForActivityResult라는 함수는 ActivityResultLauncher 객체를 생성하는데 사용이 되고,
        // 이때 넣어야 하는 두가지 인수가 ActivityResultContracts. 즉, 어떤 계약인지를 첫번째 인수로 넣고,
        // 그 다음에는 CallBack 함수를 넣는다.
        // result를 받았을 때, 나머지 코드의 CallBack 함수가 실행된다는 뜻이다.
        // <전체 흐름>
        // ActivityResultContracts의 경우엔 StartActivityForResult() 뿐만 아니라 여러 개가 있다.
        // 이번에는 Activity에 Result를 받기 위해서 Activity를 시작하기 때문에 StartActivityForResult 계약을 넣어 주었다.
        // Callback 함수로는, Result가 제대로 왔으면, isLocationServicesAvailable()를 통해 Location 서비스가 허용됐는지를 확인하고,
        // 허용이 됐으면 isRunTimePermissionsGranted()를 통해 RunTimePermissions가 허용됐는지 확인하고
        // 그게 아니라면, Toast를 띄우고 앱을 종료 하는 Logic.
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { // 이때 받아오는 Result 값을 받아왔을 때 result의 resut code가 activity.RESULT_OK이면
                result ->
            if (result.resultCode == Activity.RESULT_OK) { // 설정이 됐다는 의미가 아니라, Activity에서 잘 돌아왔다는 의미.
                // GPS를 다시 확인해서 GPS가 켜졌으면
                if (isLocationServicesAvailable()) {
                    // Permission을 다시 확인
                    isRunTimePermissionsGranted()
                } else { // 아니면 아까처럼 Toast를 통해서 User에게 메세지를 보여주고 App을 꺼준다.
                    Toast.makeText(
                        this@MainActivity,
                        "Location service is not available.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
        // Activity result launcher 실행
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        // 나중에 String 클래스에 입력.
        builder.setTitle("Inactivate Location Service")
        builder.setMessage("Location Service is turned off. Please activate location service to use app.")
        builder.setCancelable(true) // Dialog 창 바깥을 터치했을 때 창이 닫힘.
        // Callback에서 dialogInterface, i 두개를 인수로 받음.
        builder.setPositiveButton("Setting", DialogInterface.OnClickListener{ dialogInterface, i ->
            // Setting에서 ACTION_LOCATION_SOURCE_SETTINGS로 들어가는 intent를 만들어줌
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            // 아까 만들었던 Activity Result Launcher에서 Launch를 해줌.
            // 어디로 가는지는 callGPSSettingIntent에서 정의해줌.
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })
        // 취소 버튼 설정
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            // Dialog를 꺼줌.
            dialogInterface.cancel()
            Toast.makeText(
                this@MainActivity,
                "Location service is not available.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        })
        // 이걸 해줘야 뜬다.
        builder.create().show()
    }
}
