package com.example.scapp_scouting

import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.scapp_scouting.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Aktion nach und während der Eingabe im Suchfeld
        searchView = findViewById(R.id.navigationSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setNewLocation(query)
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val geocoder = Geocoder(this)

        // Map-Settings
        mMap.uiSettings.isMapToolbarEnabled = false // Toolbar ("Open Maps and Route to - Symbols")
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE // Map-Style


        // Geocoding and Marker
        val coordinates = geocoder.getFromLocationName("Furtwangen,I-Bau", 1)  // add these two lines
        val location = LatLng(coordinates[0].latitude, coordinates[0].longitude)
        mMap.addCircle(
            CircleOptions()
                .center(location)
                .radius(1000.0)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
        mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(coordinates[0].postalCode.toString() + " " + coordinates[0].featureName.toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_bounded)) // alternatively: marker_icon
                .snippet(coordinates[0].countryName.toString())
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f))
    }



    fun setNewLocation(newLocation: String){
       val geocoder = Geocoder(this)
       try {
           val coordinates = geocoder.getFromLocationName(newLocation, 1)  // add these two lines
           val location = LatLng(coordinates[0].latitude, coordinates[0].longitude)
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f))
           mMap.clear()                             //Bestehende Kreise und Marker entfernen
           mMap.addCircle(                          //Kreis im Standard-Radius einfügen
               CircleOptions()
                   .center(location)
                   .radius(1000.0)
                   .strokeColor(Color.RED)
                   .fillColor(0x22ff0000)
           )

           //TODO: Alle möglichen Marker im Umkreis aus der Datenbank laden

       } catch (e: Exception) {
           Log.e("MapErrors", "Keine Koordinaten zur Zieleingabe gefunden. Fehlermeldung: " + e);
       }

    }

    fun changeMapType(view: View){
        if(mMap.mapType == GoogleMap.MAP_TYPE_SATELLITE){
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
        else if(mMap.mapType == GoogleMap.MAP_TYPE_NORMAL)
        {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        else{
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }
}