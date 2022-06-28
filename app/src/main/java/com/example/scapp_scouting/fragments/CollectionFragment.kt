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

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<CollectionAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        var mRecyclerView = view?.findViewById<View>(R.id.locationsRecyclerView)
        super.onViewCreated(itemView, savedInstanceState)
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = CollectionAdapter()
        }
    }
}