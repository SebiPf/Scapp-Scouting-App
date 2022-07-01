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

    //Globale Variablen
    companion object {
        lateinit var globalCurrentMapLocation: LatLng
        var globalCurrentPosts: MutableList<String> = mutableListOf()
        var globalCurrentSearchPosts: MutableList<String> = mutableListOf()
        var globalOwnPosts: MutableList<String> = mutableListOf()
    }

    //Lokale Variablen für MainActivity
    private val mapsFragment = MapsFragment()
    private val collectionFragment = CollectionFragment()
    private val profileFragment = ProfileFragment()
    private lateinit var bottomNavigation: BottomNavigationView
    private var statusCollectionOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        addFragment(collectionFragment)
        addFragment(profileFragment)
        addFragment(mapsFragment)

        showHideFragment(mapsFragment)     //Startbildschirm

        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener {
            //statusCollectionOpened prevents a new call and with it an error
            if (!statusCollectionOpened) {
                when (it.itemId) {
                    R.id.maps -> showHideFragment(mapsFragment)
                    R.id.collection -> showHideFragment(collectionFragment)
                    R.id.profile_username -> showHideFragment(profileFragment)
                }
            } else {
                when (it.itemId) {
                    R.id.maps -> showHideFragment(mapsFragment)
                    R.id.profile_username -> showHideFragment(profileFragment)
                }
            }
            true
        }

    }


    private fun addFragment(fragment: Fragment) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        } catch (e: Exception) {
        }
    }

    private fun showHideFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        //Animation sadly leads to error whilst loading O.o
        /*ft.setCustomAnimations(
            android.R.animator.fade_in,
            android.R.animator.fade_out
        )*/
        if (fragment == mapsFragment) {
            statusCollectionOpened = false
            ft.remove(collectionFragment)
            ft.hide(profileFragment)
            ft.show(mapsFragment)
        } else if (fragment == collectionFragment) {
            statusCollectionOpened = true
            addFragment(collectionFragment)
            ft.hide(mapsFragment)
            ft.hide(profileFragment)
            ft.show(collectionFragment)
        } else if (fragment == profileFragment) {
            statusCollectionOpened = false
            ft.hide(mapsFragment)
            ft.remove(collectionFragment)
            ft.show(profileFragment)
        }
        ft.commit()
    }
}