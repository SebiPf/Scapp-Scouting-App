package com.example.scapp_scouting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class OwnCollectionList : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var mOwnRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_own_collection_list)
        auth = FirebaseAuth.getInstance()
        try {
            var tempUId = auth.currentUser?.uid.toString()
            mOwnRecyclerView = this.findViewById<View>(R.id.ownLocationsRecyclerView) as RecyclerView

            mOwnRecyclerView.apply {
                this.layoutManager = LinearLayoutManager(this.context)
                this.adapter = OwnCollectionAdapter(this.context, tempUId)
            }
        }catch (e:Exception){}

    }
}