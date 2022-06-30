package com.example.scapp_scouting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scapp_scouting.CollectionAdapter
import com.example.scapp_scouting.MainActivity
import com.example.scapp_scouting.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class CollectionFragment : Fragment() {

    lateinit var mRecyclerView: RecyclerView

    //Variablen für die Datenbank
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        mRecyclerView = view?.findViewById<View>(R.id.locationsRecyclerView) as RecyclerView
        val listSearchView = view?.findViewById<View>(R.id.listSearchView)

        checkSearchView(listSearchView as SearchView)
        try {
            mRecyclerView.apply {
                this.layoutManager = LinearLayoutManager(requireContext())
                this.adapter = CollectionAdapter(this.context)
            }
        }catch (e:Exception){}
    }

    //Funktionen für die Suche
    private fun checkSearchView(search: SearchView) {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                selectSearchedLocations(query)
                search.setQuery("", false)
                search.isIconified = true
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
    }

    private fun selectSearchedLocations(query: String){
        if (query.uppercase(Locale.getDefault()) == "ALLE" || query.uppercase(Locale.getDefault()) == "ALL") {
            RecyclerViewReload()
        } else {
            MainActivity.globalCurrentSearchPosts.clear()
            for(item in MainActivity.globalCurrentPosts) {
                db.collection("Posts")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            try {
                                var searchFitsTitle = query.uppercase(Locale.getDefault()) in (document.data["Title"] as String).uppercase(Locale.getDefault())
                                var searchFitsDescription = query.uppercase(Locale.getDefault()) in (document.data["Description"] as String).uppercase(Locale.getDefault())
                                var searchFits = false
                                if(searchFitsTitle) { searchFits = true }
                                if(searchFitsDescription) { searchFits = true }

                                if (document.id == item && searchFits) {
                                    MainActivity.globalCurrentSearchPosts.add(document.id)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
            }
            mRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun RecyclerViewReload(){
        MainActivity.globalCurrentSearchPosts.clear()
        MainActivity.globalCurrentSearchPosts = MainActivity.globalCurrentPosts
        mRecyclerView.adapter?.notifyDataSetChanged()
    }
}