package com.example.sportify

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sportify.util.NavigateUtility
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


private lateinit var firestore: FirebaseFirestore

object FirebaseManager {
    val authInstance: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}
class LoginActivity : AppCompatActivity() {

    /**
     * GPS Permission
     */
    lateinit var locationProvider : LocationProvider

    // PERMISSINS_REQUEST_CODE: PERMISSIONS의 ID값이라고 생각하면 편함.
    // Response를 받았을 때 100이 온다면 우리가 요청한 2가지 Request에 대한 Response가 왔구나 라는 걸 알 수 있음.
    private val PERMISSIONS_REQUEST_CODE = 100

    var latitude : Double? = 0.0
    var longitude : Double? = 0.0

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var getGPSPermissionLauncher : ActivityResultLauncher<Intent>

    val startMapActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            latitude = result.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            longitude = result.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
        }
    }
    /**
     * GPS Permission End
     */

    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "GoogleLogin"

    // 액티비티가 생성될 때 호출되는 함수입니다.
    override fun onCreate(savedInstanceState: Bundle?) {
        checkAllPermissions()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        firebaseAuth = FirebaseManager.authInstance
        firestore = FirebaseFirestore.getInstance()

        // 로그인 상태를 확인하기 위한 SharedPreferences 설정
        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IsLoggedIn", false)

        // 이미 로그인되어 있다면 로그인 성공 화면으로 이동
        if (isLoggedIn && firebaseAuth.currentUser != null) {
            // NavigateUtility().goToStartActivity(this)
            // 만약 로그아웃을 하고 싶다면 위에 코드를 사용, 그리고 SIGNOUT FOR DEV 클릭 후 재실행 NavigateUtility().goToMainActivity(this)
            finish()
        }

        // Google 로그인 버튼 설정
        val googleLoginButton = findViewById<Button>(R.id.btnGoogle)
        googleLoginButton.setOnClickListener {

            signInWithGoogle()
        }
    }

    // Google 로그인을 위한 함수입니다.
    private fun signInWithGoogle() {
        CoroutineScope(Dispatchers.IO).launch {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
            Log.d("fucking", "$googleSignInClient")
            val signInIntent = googleSignInClient.signInIntent
            Log.d("fucking", "$signInIntent")
            startActivityForResult(signInIntent, RC_SIGN_IN)
            Log.d("fucking", "$signInIntent + 1")
        }
    }

    // Google 로그인 결과를 처리하는 함수입니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAGGG", "$resultCode")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("TAGGG", "${firebaseAuth.currentUser.toString()}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google 로그인 실패: ${e.statusCode}", e)
            }

        }

    }

    // Google 로그인 인증 정보를 Firebase로 전달하는 함수입니다.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:성공")
                    val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("IsLoggedIn", true).apply()
                    val user = firebaseAuth.currentUser
                    user?.let{
                        updateUserToFirebase(it.uid, it.displayName, it.email)
                    }
                    NavigateUtility().goToStartActivity(this)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:실패", task.exception)
                }
            }
    }

    // Firebase에 사용자 정보를 업데이트하는 함수입니다.
    private fun updateUserToFirebase(userId: String, name: String?, email: String?){
        val userMap = hashMapOf(
            "name" to name,
            "email" to email
        )
        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "Firestore에 사용자 정보 업데이트 성공")
            }
            .addOnFailureListener{e ->
                Log.d(TAG, "Firestore에 사용자 정보 업데이트 실패")
            }
    }

    // 로그인 성공 후 화면으로 이동하는 함수입니다.

    /**
     * GPS Permission Map
     */

    private fun getCurrentAddress(latitude: Double, longitude: Double): Address? {
        // geoCoder 객체 생성. context 전달. Locale.getDefault()에서 현재 사용하고 있는 기기의 default 언어를 가져옴. Emulator는 영어.
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?

        addresses = try {
            geoCoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(this, "Geo-coder service is not available", Toast.LENGTH_LONG).show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "Invalid latitude, longitude", Toast.LENGTH_LONG).show()
            return null
        }

        if (addresses == null || addresses.isEmpty()) {
            Toast.makeText(this, "Address is not found", Toast.LENGTH_LONG).show()
            return null
        }
        return addresses[0]
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
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

        // 둘 중 하나라도 권한이 없다면
        if(hasFineLocationPermission!= PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // 내 기기 위치에 엑세스 하도록 허용하시겠습니까? 라는 Dialogue 팝업.
            // REQUIRED_PERMISSIONS: Permission을 요청할 List
            // PERMISSIONS_REQUEST_CODE: 요청할 코드(Request를 보내는 사람이 누군지 이름을 명시.)
            ActivityCompat.requestPermissions(this@LoginActivity,REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE)
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
                Toast.makeText(this@LoginActivity, "Permission denied. Please accept permissions.", Toast.LENGTH_LONG).show()
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
                        this@LoginActivity,
                        "Location service is not available.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
        // Activity result launcher 실행
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
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
                this@LoginActivity,
                "Location service is not available.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        })
        // 이걸 해줘야 뜬다.
        builder.create().show()
    }
    /**
     * GPS Permission End
     */


    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
