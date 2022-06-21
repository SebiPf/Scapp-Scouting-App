package com.example.scapp_scouting.fragments

import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.scapp_scouting.CreateMarker
import com.example.scapp_scouting.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.widget.TextView
import androidx.appcompat.widget.SearchView


class MapsFragment : Fragment() {

    private lateinit var currentMapLocation: LatLng
    private var currentCircleRadius: Double = 1000.0
    private lateinit var gMap: GoogleMap

    private lateinit var infoWindow : View
    private var infoWindowStatus = false

    private val callback = OnMapReadyCallback { googleMap ->
        gMap = googleMap
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val geocoder = Geocoder(this.context)

        // Map-Settings
        googleMap.uiSettings.isMapToolbarEnabled = false // Toolbar ("Open Maps and Route to - Symbols")
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE // Map-Style

        // Geocoding and Marker
        val coordinates = geocoder.getFromLocationName("Furtwangen,I-Bau", 1)  // add these two lines
        currentMapLocation = LatLng(coordinates[0].latitude, coordinates[0].longitude)
        googleMap.addCircle(             // Generierung des ersten Kreises, bzw. Radius für die Anzeige von Markern
            CircleOptions()
                .center(currentMapLocation)
                .radius(1000.0)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
        googleMap.addMarker(             // Test zum Setzen eines Markers
            MarkerOptions()
                .position(currentMapLocation)
                //.title(coordinates[0].postalCode.toString() + " " + coordinates[0].locality)
                .title("I-Bau HFU")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_bounded)) // alternatively: marker_icon
                //.snippet(coordinates[0].countryName.toString())
                .snippet(coordinates[0].locality.toString())
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, 14.0f))
        googleMap.setOnMapClickListener {

            OpenCreateMarker(latLng = it)
        }

        var infoWindowTitle = view?.findViewById<TextView>(R.id.markerInfoTitle)
        var infoWindowSnippet = view?.findViewById<TextView>(R.id.markerInfoSnippet)
        googleMap.setOnMarkerClickListener { marker ->
            if(infoWindowStatus == false) {
                slideUp(infoWindow)
                infoWindowStatus = true
                if (infoWindowTitle != null) {
                    infoWindowTitle.text = marker.title.toString()
                }
                if (infoWindowSnippet != null) {
                    infoWindowSnippet.text = marker.snippet.toString()
                }
            }
            else {
                slideDown(infoWindow)
                infoWindowStatus = false
            }
            true
        }

        //TODO: currentMapLocation mit aktuellem Standort definieren?
    }

    fun slideDown(view: View) {
        view.animate()
            .translationY(view.height.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // superfluous restoration
                    view.visibility = View.GONE
                    view.alpha = 1f
                    view.translationY = 0f
                }
            })
    }
    fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        if (view.height > 0) {
            slideUpNow(view)
        } else {
            // wait till height is measured
            view.post { slideUpNow(view) }
        }
    }

    private fun slideUpNow(view: View) {
        view.translationY = view.height.toFloat()
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                    view.alpha = 1f
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_maps, container, false)

        val btnMapType = view.findViewById<View>(R.id.btnChangeMapType)
        btnMapType.setOnClickListener { changeMapType() }

        val btnIncreaseRadius = view.findViewById<View>(R.id.btnIncreaseRadius)
        btnIncreaseRadius.setOnClickListener { increaseCircleRadius() }

        val btnDecreaseRadius = view.findViewById<View>(R.id.btnDecreaseRadius)
        btnDecreaseRadius.setOnClickListener { decreaseCircleRadius() }

        infoWindow = view.findViewById<View>(R.id.markerInfoWindow);

        //Aktion nach und während der Eingabe im Suchfeld
        val searchView = view.findViewById<View>(R.id.navigationSearchView)
        checkSearchView(searchView as SearchView)

        return view
    }

    private fun checkSearchView(search: SearchView) {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setNewLocation(query.toString())
                Log.i("TAG","Press querysubmit")
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("TAG","Press querytextchange")
                return true
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    fun setNewLocation(newLocation: String){
        val geocoder = Geocoder(this.context)
        try {
            val coordinates = geocoder.getFromLocationName(newLocation, 1)  // add these two lines
            currentMapLocation = LatLng(coordinates[0].latitude, coordinates[0].longitude)
            currentCircleRadius = 1000.0             // Zurücksetzen des Circle-Radius
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, 14.0f))
            gMap.clear()                             // Bestehende Kreise und Marker entfernen
            gMap.addCircle(                          // Kreis im Standard-Radius einfügen
                CircleOptions()
                    .center(currentMapLocation)
                    .radius(currentCircleRadius)
                    .strokeColor(Color.RED)
                    .fillColor(0x22ff0000)
            )
            updateMarkerInRadius()                   // Marker im Radius neu setzen

        } catch (e: Exception) {
            Log.e("MapErrors", "Keine Koordinaten zur Zieleingabe gefunden. Fehlermeldung: $e")
        }
    }

    fun increaseCircleRadius(){
        currentCircleRadius += 200               // Radius vergrößern
        updateCircleRadius(currentCircleRadius)
    }

    fun decreaseCircleRadius(){
        currentCircleRadius -= 200               // Radius verkleinern
        updateCircleRadius(currentCircleRadius)
    }

    private fun updateCircleRadius(radius: Double){
        gMap.clear()                             // Bestehende Kreise und Marker entfernen
        gMap.addCircle(                          // Kreis mit vorgegebenem Radius einfügen
            CircleOptions()
                .center(currentMapLocation)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
        updateMarkerInRadius()                   // Marker im Radius neu setzen
    }

    private fun updateMarkerInRadius(){
        // TODO: Hier dann jedes mal alle Marker im Radius laden
        // Überprüfung der Marker innerhalb des Radius mit getDistanceBetweenTwoPoints()
    }
    fun changeMapType(){
        when (gMap.mapType) {
            GoogleMap.MAP_TYPE_SATELLITE -> {
                gMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            GoogleMap.MAP_TYPE_NORMAL -> {
                gMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            else -> {
                gMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
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
    fun OpenCreateMarker(latLng: LatLng){

        val intent = Intent(this.context, CreateMarker::class.java)

        val lat = latLng.latitude.toString()
        val lng = latLng.longitude.toString()

        intent.putExtra("latetude", lat)
        intent.putExtra("longetude", lng)
        startActivity(intent)
    }


}