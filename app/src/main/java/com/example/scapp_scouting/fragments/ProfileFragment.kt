package com.example.scapp_scouting.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.scapp_scouting.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    //Variablen für die Datenbank
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val userNameTextview = view?.findViewById<View>(R.id.profile_username) as TextView
        userNameTextview.text = auth.currentUser?.email

        val userIdTextview = view?.findViewById<View>(R.id.profile_userid) as TextView
        userIdTextview.text = "User-ID: ${auth.currentUser?.uid}"

        return view
    }

}