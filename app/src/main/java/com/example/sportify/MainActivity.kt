package com.example.sportify

import HomeFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sportify.databinding.ActivityMainBinding


private const val TAG_CALENDER = "calender_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_ACCOUNT = "my_page_fragment"
private const val TAG_COMMUNITY = "community_fragment"
private const val TAG_GPS = "gps_fragment"
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
                R.id.account-> setFragment(TAG_ACCOUNT, AccountFragment())
                R.id.gps-> setFragment(TAG_GPS, GpsFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        // Hide all fragments
        for (existingFragment in manager.fragments) {
            fragTransaction.hide(existingFragment)
        }

        // Try to find the fragment by tag
        val existingFragment = manager.findFragmentByTag(tag)

        if (existingFragment == null) {
            // If the fragment doesn't exist, add it
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        } else {
            // If the fragment exists, show it
            fragTransaction.show(existingFragment)
        }

        fragTransaction.commitAllowingStateLoss()
    }
}