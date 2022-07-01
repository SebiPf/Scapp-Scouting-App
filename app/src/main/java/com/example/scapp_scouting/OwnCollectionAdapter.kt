package com.example.scapp_scouting

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class OwnCollectionAdapter(context: android.content.Context, uID: String):
    RecyclerView.Adapter<OwnCollectionAdapter.ViewHolder>() {

    //Variables for the database and displays
    private val db = Firebase.firestore
    private val mContext = context
    private val userID = uID

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTitle: TextView
        var itemImage: ImageView
        var itemDescription: TextView
        init {
            itemTitle = itemView.findViewById(R.id.card_heading)
            itemImage = itemView.findViewById(R.id.card_image)
            itemDescription = itemView.findViewById(R.id.card_descriptiontext)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        db.collection("Posts")
            .whereEqualTo("UserId", userID)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        if (document.id == MainActivity.globalOwnPosts[i]) {
                            //Loading title
                            viewHolder.itemTitle.text = document.data["Title"].toString()

                            //Loading description
                            viewHolder.itemDescription.text =
                                document.data["Description"].toString()

                            //Loading Images
                            val temp = document.data["Img"] as ArrayList<*>
                            val tempSnippet = temp[0] as String
                            val imgToken = tempSnippet.substring(32, 86)
                            FirebaseStorage.getInstance().reference.child(imgToken).downloadUrl.addOnSuccessListener {
                                val uri = it
                                val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder_01)
                                    .error(R.drawable.placeholder_02)
                                Glide.with(mContext)
                                    .load(uri)
                                    .apply(options)
                                    .into(viewHolder.itemImage)
                            }
                            viewHolder.itemView.setOnClickListener {
                                try {
                                    val intent =
                                        Intent(mContext, OwnDetailView::class.java).apply {
                                            putExtra("postID", document.id)
                                        }
                                    ContextCompat.startActivity(mContext, intent, null)
                                } catch (e: Exception){}
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "Error",
                            "Data could not be added to the CardView: $e"
                        )
                    }
                }
            }
    }

    override fun getItemCount(): Int {
        return MainActivity.globalOwnPosts.size
    }
}