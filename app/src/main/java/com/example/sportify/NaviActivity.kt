package com.example.sportify

import HomeFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sportify.databinding.ActivityNaviBinding


private const val TAG_CALENDER = "calender_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_ACCOUNT = "my_page_fragment"
private const val TAG_COMMUNITY = "Community_fragment"
private const val TAG_GPS = "Gps_fragment"
class NaviActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> setFragment(TAG_HOME, HomeFragment())
                R.id.community -> setFragment(TAG_COMMUNITY, CommunityFragment())
                R.id.calendar -> setFragment(TAG_CALENDER, CalenderFragment())
                R.id.account-> setFragment(TAG_ACCOUNT, AccountFragment())
                R.id.gps-> setFragment(TAG_GPS, AccountFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val calender = manager.findFragmentByTag(TAG_CALENDER)
        val home = manager.findFragmentByTag(TAG_HOME)
        val account = manager.findFragmentByTag(TAG_ACCOUNT)
        val gps = manager.findFragmentByTag(TAG_GPS)
        val community = manager.findFragmentByTag(TAG_COMMUNITY)

//        if (calender != null){
//            fragTransaction.hide(calender)
//        }
        calender?.let { fragTransaction.hide(it) }
        home?.let { fragTransaction.hide(it) }
        community?.let{ fragTransaction.hide(it) }
        gps?.let{ fragTransaction.hide(it) }
        account?.let{ fragTransaction.hide(it) }

        if (tag == TAG_CALENDER) calender?.let { fragTransaction.show(it) }
        else if (tag == TAG_HOME) home?.let { fragTransaction.show(it) }
        else if (tag == TAG_ACCOUNT) account?.let{ fragTransaction.show(it) }
        else if (tag == TAG_GPS)  gps?.let{ fragTransaction.hide(it) }
        else if (tag == TAG_COMMUNITY) community?.let{ fragTransaction.show(it) }

        fragTransaction.commitAllowingStateLoss()
    }
}