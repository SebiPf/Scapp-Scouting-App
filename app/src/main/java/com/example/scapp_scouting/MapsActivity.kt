package com.example.scapp_scouting

import android.graphics.Color
import android.location.Geocoder
import android.location.Location
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

    private lateinit var currentMapLocation: LatLng
    private var currentCircleRadius: Double = 1000.0

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

        //TODO: currentMapLocation mit aktuellem Standort definieren?
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val geocoder = Geocoder(this)

        // Map-Settings
        mMap.uiSettings.isMapToolbarEnabled = false // Toolbar ("Open Maps and Route to - Symbols")
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE // Map-Style

        // Geocoding and Marker
        val coordinates = geocoder.getFromLocationName("Furtwangen,I-Bau", 1)  // add these two lines
        currentMapLocation = LatLng(coordinates[0].latitude, coordinates[0].longitude)
        mMap.addCircle(             // Generierung des ersten Kreises, bzw. Radius für die Anzeige von Markern
            CircleOptions()
                .center(currentMapLocation)
                .radius(1000.0)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
        mMap.addMarker(             // Test zum Setzen eines Markers
            MarkerOptions()
                .position(currentMapLocation)
                .title(coordinates[0].postalCode.toString() + " " + coordinates[0].featureName.toString())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_bounded)) // alternatively: marker_icon
                .snippet(coordinates[0].countryName.toString())
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, 14.0f))
    }

    fun setNewLocation(newLocation: String){
       val geocoder = Geocoder(this)
       try {
           val coordinates = geocoder.getFromLocationName(newLocation, 1)  // add these two lines
           currentMapLocation = LatLng(coordinates[0].latitude, coordinates[0].longitude)
           currentCircleRadius = 1000.0             // Zurücksetzen des Circle-Radius
           mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, 14.0f))
           mMap.clear()                             // Bestehende Kreise und Marker entfernen
           mMap.addCircle(                          // Kreis im Standard-Radius einfügen
               CircleOptions()
                   .center(currentMapLocation)
                   .radius(currentCircleRadius)
                   .strokeColor(Color.RED)
                   .fillColor(0x22ff0000)
           )
           updateMarkerInRadius()                   // Marker im Radius neu setzen

       } catch (e: Exception) {
           Log.e("MapErrors", "Keine Koordinaten zur Zieleingabe gefunden. Fehlermeldung: " + e);
       }

    }

    fun increaseCircleRadius(view: View){
        currentCircleRadius += 200               // Radius vergrößern
        updateCircleRadius(currentCircleRadius)
    }

    fun decreaseCircleRadius(view: View){
        currentCircleRadius -= 200               // Radius verkleinern
        updateCircleRadius(currentCircleRadius)
    }

    fun updateCircleRadius(radius: Double){
        mMap.clear()                             // Bestehende Kreise und Marker entfernen
        mMap.addCircle(                          // Kreis mit vorgegebenem Radius einfügen
            CircleOptions()
                .center(currentMapLocation)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
        updateMarkerInRadius()                   // Marker im Radius neu setzen
    }

    fun updateMarkerInRadius(){
        // TODO: Hier dann jedes mal alle Marker im Radius laden
        // Überprüfung der Marker innerhalb des Radius mit getDistanceBetweenTwoPoints()
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

    fun getDistanceBetweenTwoPoints(start: LatLng, end: LatLng) : Float {
        val startPoint = Location("locationA")
        startPoint.setLatitude(start.latitude)
        startPoint.setLongitude(start.longitude)

        val endPoint = Location("locationA")
        endPoint.setLatitude(end.latitude)
        endPoint.setLongitude(end.longitude)

        return startPoint.distanceTo(endPoint)
    }
}