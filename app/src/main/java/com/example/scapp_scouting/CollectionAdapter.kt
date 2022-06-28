package com.example.scapp_scouting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CollectionAdapter : RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    //Testvariablen
    private val mText = arrayOf("Haus1", "Haus2")
    private val mImages = intArrayOf(R.drawable.profilbild_01, R.drawable.harold)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var itemImage: ImageView

        init {
            itemTitle = itemView.findViewById(R.id.card_heading)
            itemImage = itemView.findViewById(R.id.card_image)

            itemView.setOnClickListener {
                //
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.fragment_collection, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = mText[i]
        viewHolder.itemImage.setImageResource(mImages[i])
    }

    override fun getItemCount(): Int {
        return mText.size
    }
}