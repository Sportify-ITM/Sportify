package com.example.sportify

import HomeFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sportify.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


private const val TAG_CALENDER = "calender_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_ACCOUNT = "my_page_fragment"
private const val TAG_COMMUNITY = "community_fragment"
private const val TAG_GPS = "gps_fragment"

private val PERMISSIONS_REQUEST_CODE = 200
private val STORAGE_PERMISSIONS_REQUEST_CODE = 201 // New code for storage permissions

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> setFragment(TAG_HOME, HomeFragment())
                R.id.community -> setFragment(TAG_COMMUNITY, CommunityFragment())
                R.id.calendar -> setFragment(TAG_CALENDER, CalenderFragment())
                R.id.account-> {
                    //번들 이용해서 현재 유저의 uid를 프래그먼트로 전달하기
                    var accountFragment = AccountFragment()
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    bundle.putString("destinationUid", uid)
                    accountFragment.arguments = bundle
                    setFragment(TAG_ACCOUNT, accountFragment)
                }
                R.id.gps-> setFragment(TAG_GPS, GpsFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
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

    fun setToolbarDefault(){
        binding.toolbarUsername.visibility = View.GONE
        binding.toolbarBtnBack.visibility = View.GONE
        binding.toolbarTitleImage.visibility = View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_sign_out -> {
//                signOut()
//                true
//            }
//            R.id.action_change_team -> {
//                // Handle the action when "Change Team" is selected
//                startActivity(Intent(this, StartActivity::class.java))
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

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
        startActivity(Intent(this,LoginActivity::class.java))
    }
}