package com.example.scapp_scouting.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.scapp_scouting.MainActivity
import com.example.scapp_scouting.R


class CollectionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val centerlist = listOf("Place 01", "Place 02", "Place 03")

        var listView = view?.findViewById(R.id.location_list_view) as ListView
        val adapter = ArrayAdapter(this.context, android.R.layout.simple_list_item_1, centerlist)
        listView.adapter = adapter*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

}