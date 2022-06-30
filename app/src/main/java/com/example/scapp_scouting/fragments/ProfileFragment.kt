package com.example.scapp_scouting.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.scapp_scouting.*
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    //Variablen für die Datenbank
    private lateinit var auth: FirebaseAuth

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

        val btnOwnLocations = view.findViewById<View>(R.id.btnOwnLocations)
        btnOwnLocations.setOnClickListener {
            try {
                val intent = Intent(activity, OwnCollectionList::class.java)
                startActivity(intent)
            }catch (e: Exception){ }
        }

        val btnLogout = view.findViewById<View>(R.id.btnProfileLogout)
        btnLogout.setOnClickListener {
            try {
                auth.signOut()
                val intent = Intent(activity, Login::class.java)
                startActivity(intent)
            }catch (e: Exception){}
        }

return view
}

}