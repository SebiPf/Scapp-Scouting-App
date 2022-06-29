package com.example.scapp_scouting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scapp_scouting.CollectionAdapter
import com.example.scapp_scouting.R


class CollectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        var mRecyclerView = view?.findViewById<View>(R.id.locationsRecyclerView) as RecyclerView
        super.onViewCreated(itemView, savedInstanceState)
        mRecyclerView?.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = CollectionAdapter()
        }
    }
}