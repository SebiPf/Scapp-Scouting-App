package com.example.scapp_scouting

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import io.grpc.Context
import org.w3c.dom.Text

class CollectionAdapter(context: android.content.Context) :
    RecyclerView.Adapter<CollectionAdapter.ViewHolder>() {

    //Variablen für die Datenbank
    private val db = Firebase.firestore
    private val mContext = context

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var itemImage: ImageView
        var itemDescription: TextView

        init {
            itemTitle = itemView.findViewById(R.id.card_heading)
            itemImage = itemView.findViewById(R.id.card_image)
            itemDescription = itemView.findViewById(R.id.card_descriptiontext)

            itemView.setOnClickListener {
                //
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.location_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        db.collection("Posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        if (document.id == MainActivity.globalCurrentPosts[i]) {
                            viewHolder.itemTitle.text = document.data["Title"].toString()
                            viewHolder.itemDescription.text =
                                document.data["Description"].toString()

                            val temp = document.data["Img"] as ArrayList<*>
                            var tempSnippet = temp[0] as String
                            var imgToken = tempSnippet!!.substring(32, 86)
                            FirebaseStorage.getInstance().reference.child(imgToken).downloadUrl.addOnSuccessListener {
                                var uri = it
                                val options: RequestOptions = RequestOptions()
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder_01)
                                    .error(R.drawable.placeholder_02)
                                Glide.with(mContext)
                                    .load(uri)
                                    .apply(options)
                                    .into(viewHolder.itemImage)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "Error",
                            "Daten konnten dem CardView nicht hinzugefügt werden: $e"
                        )
                    }
                }
            }
    }

    override fun getItemCount(): Int {
        return MainActivity.globalCurrentPosts.size
    }
}