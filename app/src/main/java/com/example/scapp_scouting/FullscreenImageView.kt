package com.example.scapp_scouting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage

class FullscreenImageView : AppCompatActivity() {
    private lateinit var imgToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image_view)
        imgToken = intent.getStringExtra("imgToken").toString()
        fillViews()
    }

    //Fill data to view
    private fun fillViews() {
        FirebaseStorage.getInstance().reference.child(imgToken).downloadUrl.addOnSuccessListener {
            val uri = it
            val options: RequestOptions = RequestOptions()
                .placeholder(R.drawable.placeholder_01)
                .error(R.drawable.placeholder_02)
            Glide.with(this)
                .load(uri)
                .apply(options)
                .into(findViewById<View>(R.id.fullScreenImageView) as ImageView)
        }
    }
}