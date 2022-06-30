package com.example.scapp_scouting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.scapp_scouting.fragments.CollectionFragment
import com.example.scapp_scouting.fragments.MapsFragment
import com.example.scapp_scouting.fragments.ProfileFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val mapsFragment = MapsFragment()
    private val collectionFragment = CollectionFragment()
    private val profileFragment = ProfileFragment()
    private lateinit var bottomNavigation: BottomNavigationView

    //Globale Variablen
    companion object {
        lateinit var globalCurrentMapLocation: LatLng
        var globalCurrentPosts: MutableList<String> = mutableListOf()
        var globalCurrentSearchPosts : MutableList<String> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        addFragement(profileFragment)
        addFragement(mapsFragment)

        showHideFragment(mapsFragment) //Startbildschirm

        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.maps -> showHideFragment(mapsFragment)
                R.id.collection -> showHideFragment(collectionFragment)
                R.id.profile_username -> showHideFragment(profileFragment)
            }
            true
        }

    }


    private fun addFragement(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    private fun showHideFragment(fragment: Fragment) {
        var ft = supportFragmentManager.beginTransaction()
        /*ft.setCustomAnimations(
            android.R.animator.fade_in,
            android.R.animator.fade_out
        )*/
        if (fragment == mapsFragment) {
            ft.remove(collectionFragment)
            ft.hide(profileFragment)
            ft.show(mapsFragment)
        } else if (fragment == collectionFragment) {
            addFragement(collectionFragment)
            ft.hide(mapsFragment)
            ft.hide(profileFragment)
            ft.show(collectionFragment)
        } else if (fragment == profileFragment) {
            ft.hide(mapsFragment)
            ft.remove(collectionFragment)
            ft.show(profileFragment)
        }
            ft.commit()
    }
}