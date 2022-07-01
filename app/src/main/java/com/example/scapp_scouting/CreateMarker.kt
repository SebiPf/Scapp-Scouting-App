package com.example.scapp_scouting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import javax.annotation.Nullable
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreateMarker : AppCompatActivity() {

    private var GALLERY_REQ_CODE = 1
    private val imageList = ArrayList<Uri>()
    private var imgnames: MutableList<String> = ArrayList()
    private lateinit var auth: FirebaseAuth

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_marker)
        auth = FirebaseAuth.getInstance()
        val imgButton= findViewById<Button>(R.id.ImgButton)
        imgButton.setOnClickListener {
            selectImage()
        }
        val uploadButton= findViewById<Button>(R.id.popup_window_button)
        uploadButton.setOnClickListener {
            upload()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, GALLERY_REQ_CODE)
    }
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
                if (data != null) {
                    if (data.clipData != null) {
                        val count: Int? = data.clipData?.itemCount
                        var currentImageSelect = 0
                        while (currentImageSelect < count!!) {
                            val imageUri: Uri? = data.clipData?.getItemAt(currentImageSelect)?.getUri()
                            if (imageUri != null) {
                                imageList.add(imageUri)
                            }
                            val currImgHelp = currentImageSelect
                            val imageView = ImageView(this)
                            imageView.layoutParams = RelativeLayout.LayoutParams(200, 200)
                            imageView.id = currentImageSelect
                            imageView.x = 10F
                            imageView.y = 10F
                            val layout = findViewById<LinearLayout>(R.id.imgviewcontainer)
                            layout.addView(imageView)
                            if(currImgHelp<4){
                                val imgInLayout = layout.getChildAt(currImgHelp+1) as ImageView
                                imgInLayout.setImageURI(imageUri)
                            }
                            if(count>4){
                                val textView = findViewById<TextView>(R.id.imgcount)
                                val num = count -4
                                textView.text = "+$num"
                            }
                            currentImageSelect += 1
                        }
                    }
                    else {
                        val imageUri: Uri? = data.data!!
                        if (imageUri != null) {
                            imageList.add(imageUri)
                            val imageView = ImageView(this)
                            imageView.layoutParams = RelativeLayout.LayoutParams(500, 500)
                            imageView.x = 10F
                            imageView.y = 10F
                            val layout = findViewById<LinearLayout>(R.id.imgviewcontainer)
                            layout.addView(imageView)
                            imageView.setImageURI(imageUri)
                        }
                    }
        }
    }

    private fun upload(){

        val intent = intent
        val post: MutableMap<String, Any> = HashMap()
        val imageFolder = FirebaseStorage.getInstance().reference.child("ImageFolder")
            var uploads = 0
            while (uploads < imageList.size) {
                val uuid = UUID.randomUUID().toString()
                imgnames.add("gs://scapp-scouting.appspot.com/ImageFolder/image/$uuid")
                val imageName: StorageReference =
                    imageFolder.child("image/$uuid")
                imageName.putFile(imageList[uploads])
                uploads++
            }
        post["Img"] = imgnames

        val latitudeUpload = intent.getStringExtra("latitude")
        val longitudeUpload = intent.getStringExtra("longitude")
        val latlng = "$latitudeUpload, $longitudeUpload"
        val position = latlng.split(",").toTypedArray()
        val latitude = position[0].toDouble()
        val longitude = position[1].toDouble()
        val location = LatLng(latitude, longitude)
        post["Coordinates"] = location

        var text   = findViewById<EditText>(R.id.popup_window_text)
        val description = text.text.toString()
        post["Description"] = description

        text   = findViewById<EditText>(R.id.popup_window_title)
        val title = text.text.toString()
        post["Title"] = title

        val userid = auth.currentUser?.uid.toString()
        post["UserId"] = userid

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("Posts")
            .add(post)
            .addOnSuccessListener { }
            .addOnFailureListener { e -> Log.w( "Error adding document", e) }
        finish()
    }

}



