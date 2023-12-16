package com.example.sportify
import ActionBarUtility
import HomeFragment
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sportify.databinding.ActivityMainBinding
import com.example.sportify.db.AppDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


private const val TAG_CALENDAR = "calendar_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_ACCOUNT = "my_page_fragment"
private const val TAG_COMMUNITY = "community_fragment"
private const val TAG_GPS = "gps_fragment"
private val PERMISSIONS_REQUEST_CODE = 200
private val STORAGE_PERMISSIONS_REQUEST_CODE = 201 // New code for storage permissions

class MyFirebaseMessagingService : FirebaseMessagingService()

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Use ActionBarUtility to set the logo
        GlobalScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getInstance(applicationContext)
            val selectedTeam = database.teamDao().getSelectedTeam()
            selectedTeam?.let {
                runOnUiThread {
                    ActionBarUtility.setLogo(this@MainActivity, it.teamId)
                }
            }
        }

        setFragment(TAG_HOME, HomeFragment())

        registerPushToken()
        binding.navigationView.setOnItemSelectedListener { item ->
            setToolbarDefault()
            when (item.itemId) {
                R.id.home -> setFragment(TAG_HOME, HomeFragment())
                R.id.community -> setFragment(TAG_COMMUNITY, CommunityFragment())
                R.id.calendar -> setFragment(TAG_CALENDAR, CalenderFragment())
                R.id.account -> {

                    //번들 이용해서 현재 유저의 uid를 프래그먼트로 전달하기
                    var accountFragment = AccountFragment()
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    bundle.putString("destinationUid", uid)
                    accountFragment.arguments = bundle
                    setFragment(TAG_ACCOUNT, accountFragment)
                }

                R.id.gps -> setFragment(TAG_GPS, GpsFragment())
            }
            true
        }


    }
    fun registerPushToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val map = mutableMapOf<String, Any>()
                map["pushToken"] = token!!

                FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
            }
        }
    }

    fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        // Try to find the fragment by tag
        val existingFragment = manager.findFragmentByTag(tag)

        if (existingFragment == null) {
            // If the fragment doesn't exist, add it
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        } else {
            // If the fragment exists, show it
            fragTransaction.show(existingFragment)
        }

        // Hide all fragments
        for (existingFragment in manager.fragments) {
            if (existingFragment.tag == tag) {
                fragTransaction.show(existingFragment)
            } else {
                fragTransaction.hide(existingFragment)
            }
        }
        fragTransaction.commitAllowingStateLoss()
        setToolbarDefault()
    }

    fun setToolbarDefault() {
        binding.toolbarUsername.visibility = View.GONE
        binding.toolbarBtnBack.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                return true
            }
            R.id.action_change_team -> {
                startActivity(Intent(this, StartActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun signOut() {
        //firebaseAuth.signOut()
        FirebaseManager.authInstance.signOut()
        // Optionally, also sign out from Google if you're using Google Sign-In
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
        startActivity(Intent(this, LoginActivity::class.java))}


        //프로필 사진 고르기 콜백
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == AccountFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
                var imageUri = data?.data
                var uid = FirebaseAuth.getInstance().currentUser?.uid
                var storageRef =
                    FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
                storageRef.putFile(imageUri!!)
                    .continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                        return@continueWithTask storageRef.downloadUrl
                    }.addOnSuccessListener { uri ->
                    var map = HashMap<String, Any>()
                    map["image"] = uri.toString()
                    FirebaseFirestore.getInstance().collection("profileImages").document(uid)
                        .set(map)
                }
            }
        }

    }