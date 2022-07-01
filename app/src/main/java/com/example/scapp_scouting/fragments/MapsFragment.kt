package com.example.scapp_scouting.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.scapp_scouting.CreateMarker
import com.example.scapp_scouting.ListDetailView
import com.example.scapp_scouting.MainActivity
import com.example.scapp_scouting.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_maps.*


class MapsFragment : Fragment() {

    //Variables for the map
    private lateinit var currentMapLocation: LatLng
    private var currentCircleRadius: Double = 1000.0
    private lateinit var gMap: GoogleMap
    private val radiusChangeFactor = 500
    private var zoomFactor = 14.0f
    private val startLocation = "Furtwangen"
    private val startMapType = GoogleMap.MAP_TYPE_SATELLITE

    //Variables for the InfoWindow
    private lateinit var infoWindow: View
    private var markerSelected = ""
    private var infoWindowStatus = false

    //Variables for LocationAdd
    private var addLocationStatus = false

    //Variables for the database
    private val db = Firebase.firestore

    //Variables for database queries
    private lateinit var auth: FirebaseAuth

    //Map-Initialization and relating functions
    private val callback = OnMapReadyCallback { googleMap ->
        //Initialization
        gMap = googleMap
        val geocoder = Geocoder(this.context)

        //Map settings
        gMap.uiSettings.isMapToolbarEnabled =
            false        // Toolbar ("Open Maps and Route to - Symbols")
        gMap.mapType = startMapType                        // Map-Style

        //Geocoding and Marker
        val coordinates = geocoder.getFromLocationName(startLocation, 1)
        setCurrentMapLocation(LatLng(coordinates[0].latitude, coordinates[0].longitude))

        //Starting radius for the search (currently 1km)
        showCircleWithRadius(currentCircleRadius)

        //Load all markers in the initial radius
        showAndBindMarkerInRadius()

        //Camera moves to the starting point of the map (currentMapLocation)
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, zoomFactor))

        //Listener for clicks on the map
        gMap.setOnMapClickListener {
            //infoWindow disappears when you click on the map
            infoWindowStatus = false
            markerSelected = ""
            slideDown(infoWindow)

            //Window for adding a new location will be opened
            //(when status is activated -> click on NewLocation-Button before)
            if (addLocationStatus) {
                addLocationStatus = false
                slideUp(btnAddLocation)
                openCreateMarker(latLng = it)
            }
        }

        //InfoWindow (displayed when marker is clicked)
        val infoWindowTitle = view?.findViewById<TextView>(R.id.markerInfoTitle)
        val infoWindowImage = view?.findViewById<ImageView>(R.id.markerInfoImage)
        gMap.setOnMarkerClickListener { marker ->
            //When infoWindow is closed
            //InfoWindow with data of the marker will be shown
            if (!infoWindowStatus) {
                infoWindowStatus = true
                markerSelected = marker.id
                Log.i("id2", "---")
                Log.i("id2", "MarkerID: " + marker.id)
                Log.i("id2", "MarkerSelected: " + markerSelected)

                slideUp(infoWindow)
                if (infoWindowTitle != null) {
                    infoWindowTitle.text = marker.title.toString()
                }
                if (infoWindowImage != null && marker.snippet != null) {
                    try {
                        infoWindow.setOnClickListener() {
                            val idToken = marker.snippet!!.substring(89, 109)
                            slideDown(infoWindow)
                            infoWindowStatus = false
                            markerSelected = ""
                            view?.let { it1 -> openDetailView(it1.context, idToken) }
                        }

                        val imgToken = marker.snippet!!.substring(32, 86)
                        FirebaseStorage.getInstance().reference.child(imgToken).downloadUrl.addOnSuccessListener {
                            val uri = it
                            val options: RequestOptions = RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_01)
                                .error(R.drawable.placeholder_02)
                            Glide.with(this)
                                .load(uri)
                                .apply(options)
                                .into(infoWindowImage)
                        }
                    } catch (e: Exception) {
                        Glide.with(this)
                            .load(R.drawable.placeholder_02)
                            .into(infoWindowImage)
                    }
                }
            }
            //When infoWindow is shown and click is on a new marker
            //Slides up a infoWindow with data of the new marker
            else if (markerSelected != marker.id) {
                //slideDown(infoWindow)
                infoWindowStatus = true
                markerSelected = marker.id
                slideUp(infoWindow)
                if (infoWindowTitle != null) {
                    infoWindowTitle.text = marker.title.toString()
                }
                if (infoWindowImage != null && marker.snippet != null) {
                    try {
                        infoWindow.setOnClickListener() {
                            val idToken = marker.snippet!!.substring(89, 109)
                            slideDown(infoWindow)
                            infoWindowStatus = false
                            markerSelected = ""
                            view?.let { it1 -> openDetailView(it1.context, idToken) }
                        }

                        val imgToken = marker.snippet!!.substring(32, 86)
                        FirebaseStorage.getInstance().reference.child(imgToken).downloadUrl.addOnSuccessListener {
                            val uri = it
                            val options: RequestOptions = RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_01)
                                .error(R.drawable.placeholder_02)
                            Glide.with(this)
                                .load(uri)
                                .apply(options)
                                .into(infoWindowImage)
                        }

                    } catch (e: Exception) {
                        Log.e("MapErrors", "Image in infoWindow could not be loaded: $e")
                        Glide.with(this)
                            .load(R.drawable.placeholder_02)
                            .into(infoWindowImage)
                    }
                }
            }
            //When infoWindow is shown and click is on the same new marker again
            //Closes the infoWindow
            else {
                slideDown(infoWindow)
                infoWindowStatus = false
                markerSelected = ""
            }
            true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_maps, container, false)

        val btnMapType = view.findViewById<View>(R.id.btnChangeMapType)
        btnMapType.setOnClickListener { changeMapType() }

        val btnIncreaseRadius = view.findViewById<View>(R.id.btnIncreaseRadius)
        btnIncreaseRadius.setOnClickListener { increaseCircleRadius() }

        val btnDecreaseRadius = view.findViewById<View>(R.id.btnDecreaseRadius)
        btnDecreaseRadius.setOnClickListener { decreaseCircleRadius() }

        auth = FirebaseAuth.getInstance()

        //Wide view
        val btnRadiusWeit = view.findViewById<View>(R.id.btnRadiusWeit)
        btnRadiusWeit.setOnClickListener {
            infoWindowStatus =
                false                        // Clear infoWindow status when search changes location
            markerSelected = ""
            slideDown(infoWindow)

            currentCircleRadius = 10000000.0
            gMap.clear()
            showAndBindMarkerInRadius()
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, 4.5f))
        }

        val btnAddLocation = view.findViewById<View>(R.id.btnAddLocation)
        btnAddLocation.setOnClickListener {
            addLocation()
        }

        infoWindow = view.findViewById(R.id.markerInfoWindow)

        //Action after and during the input in the search field
        val searchView = view.findViewById<View>(R.id.navigationSearchView)
        checkSearchView(searchView as SearchView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    //Functions for location search
    private fun checkSearchView(search: SearchView) {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Sets new location when query new got submitted
                setNewLocation(query)
                //Resets the searchView (searchView and soft-Keyboard gets closed)
                search.setQuery("", false)
                search.isIconified = true
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    //Reset currentMapLocation and the relating global Variable
    private fun setCurrentMapLocation(latLng: LatLng) {
        currentMapLocation = latLng
        MainActivity.globalCurrentMapLocation = latLng
    }

    //Setting new Location with new search query
    fun setNewLocation(newLocation: String) {
        val geocoder = Geocoder(this.context)
        try {
            val coordinates = geocoder.getFromLocationName(newLocation, 1)
            setCurrentMapLocation(LatLng(coordinates[0].latitude, coordinates[0].longitude))
            currentCircleRadius = 1000.0                    // Reset the circle radius
            zoomFactor = 14.0f
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, zoomFactor))
            gMap.clear()                                    // Remove existing circles and markers

            infoWindowStatus =
                false                        // Clear infoWindow status when search changes location.
            markerSelected = ""
            slideDown(infoWindow)

            showCircleWithRadius(currentCircleRadius)       // Show circle after resetting the radius
            showAndBindMarkerInRadius()                     // Reset marker in radius
        } catch (e: Exception) {
            Log.e("MapErrors", "No coordinates found for destination input. Error message: $e")
        }
    }

    //Selection and display of markers in the radius
    //Current markers in the radius are stored in global lists for query at other locations (list, ...)
    private fun showAndBindMarkerInRadius() {
        //Delete previous entries in the global list
        MainActivity.globalCurrentPosts.clear()
        MainActivity.globalCurrentSearchPosts.clear()
        MainActivity.globalCurrentOwnPosts.clear()

        //Search and insert new entries
        db.collection("Posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val tempCoordinates =
                        document.data.getValue("Coordinates") as HashMap<Double, Double>
                    val tempLatitude = tempCoordinates.getValue(tempCoordinates.keys.first())
                    val tempLongitude = tempCoordinates.getValue(tempCoordinates.keys.last())
                    val tempMarkerPosition = LatLng(tempLatitude, tempLongitude)

                    var tempSnippet = ""
                    try {
                        val temp = document.data["Img"] as ArrayList<*>
                        tempSnippet = temp[0] as String
                        tempSnippet = tempSnippet + " - " + document.id
                    } catch (e: Exception) {
                        Log.e("Error", "Snippet could not be attached to the marker: $e")
                    }

                    if (getDistanceBetweenTwoPoints(
                            currentMapLocation,
                            tempMarkerPosition
                        ) <= currentCircleRadius
                    ) {
                        MainActivity.globalCurrentPosts.add(document.id)
                        MainActivity.globalCurrentSearchPosts.add(document.id)

                        if (document.data["UserId"].toString() == auth.currentUser?.uid) {
                            MainActivity.globalCurrentOwnPosts.add(document.id)
                        }

                        addMarker(
                            tempMarkerPosition,
                            document.data["Title"].toString(),
                            tempSnippet
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FirebaseGet", "Error getting documents.", exception)
            }
    }

    //Function to add a new marker to the map (with options, symbol, ...)
    private fun addMarker(position: LatLng, title: String, photoURL: String) {
        gMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_bounded)) // alternatively: marker_icon
                .snippet(photoURL)
        )
    }

    //Get the distance between two Locations (important to get all locations/ markers in the current radius)
    private fun getDistanceBetweenTwoPoints(start: LatLng, end: LatLng): Float {
        val startPoint = Location("locationA")
        startPoint.latitude = start.latitude
        startPoint.longitude = start.longitude

        val endPoint = Location("locationA")
        endPoint.latitude = end.latitude
        endPoint.longitude = end.longitude

        return startPoint.distanceTo(endPoint)
    }

    //Buttonclick to activate marker-creation
    //Slide down buttons, infoWindow and shows Toast with information
    private fun addLocation() {
        if (!addLocationStatus) {
            addLocationStatus = true
            slideDown(btnAddLocation)
            slideDown(infoWindow)
            infoWindowStatus = false
            Toast.makeText(
                this.context,
                "Klick auf die Location erstellt neuen Marker.",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    //Update shown circle on the map and resets the current markers
    private fun updateCircleRadius(radius: Double) {
        gMap.clear()                                            // Remove existing circles and markers
        gMap.addCircle(                                         // Insert circle with specified radius
            CircleOptions()
                .center(currentMapLocation)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
        showAndBindMarkerInRadius()                             // Reset marker in radius
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMapLocation, zoomFactor))
    }

    private fun increaseCircleRadius() {
        currentCircleRadius += radiusChangeFactor               // Increase radius
        zoomFactor -= 0.25f
        updateCircleRadius(currentCircleRadius)
    }

    private fun decreaseCircleRadius() {
        currentCircleRadius -= radiusChangeFactor               // Decrease radius
        zoomFactor += 0.25f
        updateCircleRadius(currentCircleRadius)
    }

    //Display and changes to the search radius
    private fun showCircleWithRadius(radius: Double) {
        gMap.addCircle(             // Generation of the first circle, or radius for the display of markers
            CircleOptions()
                .center(currentMapLocation)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(0x22ff0000)
        )
    }

    //Open CreateMarker-Activity (called when clicked on map)
    private fun openCreateMarker(latLng: LatLng) {

        val intent = Intent(this.context, CreateMarker::class.java)

        val lat = latLng.latitude.toString()
        val lng = latLng.longitude.toString()

        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lng)
        startActivity(intent)
    }

    //Open detail-view activity and passes the current postID
    private fun openDetailView(context: Context, id: String) {
        val intent = Intent(context, ListDetailView::class.java).apply {
            putExtra("postID", id)
        }
        ContextCompat.startActivity(context, intent, null)
    }

    //Change shown type of the map
    private fun changeMapType() {
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

    //Animations for the InfoWindow
    fun slideDown(view: View) {
        view.animate()
            .translationY(view.height.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
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
            //Wait till height is measured
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
}