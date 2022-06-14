package com.example.scapp_scouting

import com.google.android.gms.maps.model.LatLng

//@kotlinx.serialization.Serializable
data class Post(

    val UserId: Double,
    val Title: String,
    val Coodinates: LatLng,
    val Description: String,
    val Img: String
)
