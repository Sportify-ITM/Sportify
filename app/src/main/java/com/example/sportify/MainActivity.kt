package com.example.sportify

import HomeFragment
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sportify.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


private const val TAG_CALENDAR = "calendar_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_ACCOUNT = "my_page_fragment"
private const val TAG_COMMUNITY = "community_fragment"
private const val TAG_GPS = "gps_fragment"
private val PERMISSIONS_REQUEST_CODE = 200
private val STORAGE_PERMISSIONS_REQUEST_CODE = 201 // New code for storage permissions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            setToolbarDefault()
            when (item.itemId) {
                R.id.home -> setFragment(TAG_HOME, HomeFragment())
                R.id.community -> setFragment(TAG_COMMUNITY, CommunityFragment())
                R.id.calendar -> setFragment(TAG_CALENDAR, CalendarFragment())
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

    fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val calendar = manager.findFragmentByTag(TAG_CALENDAR)
        val home = manager.findFragmentByTag(TAG_HOME)
        val account = manager.findFragmentByTag(TAG_ACCOUNT)
        val gps = manager.findFragmentByTag(TAG_GPS)
        val community = manager.findFragmentByTag(TAG_COMMUNITY)

        calendar?.let { fragTransaction.hide(it) }
        home?.let { fragTransaction.hide(it) }
        community?.let { fragTransaction.hide(it) }
        gps?.let { fragTransaction.hide(it) }
        account?.let { fragTransaction.hide(it) }

        if (tag == TAG_CALENDAR) calendar?.let { fragTransaction.show(it) }
        else if (tag == TAG_HOME) home?.let { fragTransaction.show(it) }
        else if (tag == TAG_ACCOUNT) account?.let { fragTransaction.show(it) }
        else if (tag == TAG_GPS) gps?.let { fragTransaction.hide(it) }
        else if (tag == TAG_COMMUNITY) community?.let { fragTransaction.show(it) }

        fragTransaction.commitAllowingStateLoss()
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
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