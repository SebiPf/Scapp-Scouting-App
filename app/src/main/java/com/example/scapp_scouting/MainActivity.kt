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

    // TODO: State Handling (durch show() und hide()?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragement(collectionFragment)
        addFragement(profileFragment)
        addFragement(mapsFragment)  //Startbildschirm

        bottom_navigation = findViewById(R.id.bottom_navigation)

        bottom_navigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.maps -> showHideFragment(mapsFragment)
                R.id.collection -> showHideFragment(collectionFragment)
                R.id.profile -> showHideFragment(profileFragment)
            }
            true
        }
    }

    private fun addFragement(fragment: Fragment) {
        if(fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    fun showHideFragment(fragment: Fragment) {
        var ft = supportFragmentManager.beginTransaction()
        //evtl. Doppelung durch Fade mit mehreren aktiven Fragments
        ft.setCustomAnimations(
            android.R.animator.fade_in,
            android.R.animator.fade_out
        )
        if(fragment.equals(mapsFragment)) {
            ft.hide(collectionFragment)
            ft.hide(profileFragment)
            ft.show(mapsFragment)
        }
        else if(fragment.equals(collectionFragment)) {
            ft.hide(mapsFragment)
            ft.hide(profileFragment)
            ft.show(collectionFragment)
        }
        else if(fragment.equals(profileFragment)) {
            ft.hide(mapsFragment)
            ft.hide(collectionFragment)
            ft.show(profileFragment)
        }
        ft.commit()
    }
}