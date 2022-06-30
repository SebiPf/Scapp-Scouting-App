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
        val imgButton= findViewById(R.id.ImgButton) as Button
        imgButton.setOnClickListener {
            SelectImage()
        }
        val uploadButton= findViewById(R.id.popup_window_button) as Button
        uploadButton.setOnClickListener {
            upload()
        }
    }


    private fun SelectImage() {
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
                            val imageuri: Uri? = data.clipData?.getItemAt(currentImageSelect)?.getUri()
                            if (imageuri != null) {
                                imageList.add(imageuri)
                            }
                            val currimghelp = currentImageSelect
                            val imageView = ImageView(this)
                            imageView.layoutParams = RelativeLayout.LayoutParams(200, 200)
                            imageView.id = currentImageSelect
                            imageView.x = 10F
                            imageView.y = 10F
                            val layout = findViewById(R.id.imgviewcontainer) as LinearLayout
                            layout.addView(imageView)
                            if(currimghelp<4){
                                val imginLayout = layout.getChildAt(currimghelp+1) as ImageView
                                imginLayout.setImageURI(imageuri)
                            }
                            if(count>4){
                                val textView = findViewById(R.id.imgcount) as TextView
                                val num = count -4
                                textView.text = "+"+ num
                            }
                            currentImageSelect += 1
                        }
                    }
                    else {
                        val imageuri: Uri? = data.data!!
                        if (imageuri != null) {
                            imageList.add(imageuri)
                            val imageView = ImageView(this)
                            imageView.layoutParams = RelativeLayout.LayoutParams(500, 500)
                            imageView.x = 10F
                            imageView.y = 10F
                            val layout = findViewById(R.id.imgviewcontainer) as LinearLayout
                            layout.addView(imageView)
                            imageView.setImageURI(imageuri)
                        }
                    }
        }
    }
    private fun upload(){

        val intent = getIntent()
        val post: MutableMap<String, Any> = HashMap()
        val imageFolder = FirebaseStorage.getInstance().reference.child("ImageFolder")
            var uploads = 0
            while (uploads < imageList.size) {
                val uuid = UUID.randomUUID().toString()
                imgnames.add("gs://scapp-scouting.appspot.com/ImageFolder/image/" + uuid)
                val imagename: StorageReference =
                    imageFolder.child("image/" + uuid)
                imagename.putFile(imageList[uploads])
                uploads++
            }
        post["Img"] = imgnames

        val latitudeupload = intent.getStringExtra("latitude")
        val longitudeupload = intent.getStringExtra("longitude")
        val latlong = latitudeupload+","+longitudeupload
        val position = latlong.split(",").toTypedArray()
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
            .addOnSuccessListener { documentReference -> }
            .addOnFailureListener { e -> Log.w( "Error adding document", e) }
        finish()
    }

}



