package com.example.scapp_scouting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.scapp_scouting.fragments.CollectionFragment
import com.example.scapp_scouting.fragments.MapsFragment
import com.example.scapp_scouting.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val mapsFragment = MapsFragment()
    private val collectionFragment = CollectionFragment()
    private val profileFragment = ProfileFragment()
    private lateinit var bottom_navigation: BottomNavigationView

    override fun onCreate(savedInsanceState: Bundle?) {
        super.onCreate(savedInsanceState)
        setContentView(R.layout.activity_main)
        replaceFragement(mapsFragment)  //Startbildschirm

        bottom_navigation = findViewById(R.id.bottom_navigation)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.maps -> replaceFragement(mapsFragment)
                R.id.collection -> replaceFragement(collectionFragment)
                R.id.profile -> replaceFragement(profileFragment)
            }
            true
        }
    }

    private fun replaceFragement(fragment: Fragment) {
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}