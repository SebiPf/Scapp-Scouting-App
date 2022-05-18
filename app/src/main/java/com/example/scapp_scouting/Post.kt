package com.example.scapp_scouting

//@kotlinx.serialization.Serializable
data class Post(
    val Id: Double,
    val UserId: Double,
    val Title: String,
    //val Coodinates: Array,
    val Description: String,
    val Img: String
)
