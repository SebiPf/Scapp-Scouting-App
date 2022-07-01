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
        val imgButton = findViewById<Button>(R.id.ImgButton)
        //Event/Click Listener that starts function selectImage
        imgButton.setOnClickListener {
            selectImage()
        }
        //Event/Click Listener that starts function upload
        val uploadButton = findViewById<Button>(R.id.popup_window_button)
        uploadButton.setOnClickListener {
            upload()
        }
    }

    //create an Intent that starts the Gallery on the phone actions in Gallery returns data
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, GALLERY_REQ_CODE)
    }

    // Takes the result of the Gallery Event
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //checks wether the data received is a single image or multiple
        if (data != null) {
            if (data.clipData != null) {
                val count: Int? = data.clipData?.itemCount
                var currentImageSelect = 0

                //Takes the clipdata and adds the images to a List which is used later also displays up to 4 Images for the user
                while (currentImageSelect < count!!) {
                    val imageUri: Uri? = data.clipData?.getItemAt(currentImageSelect)?.uri
                    if (imageUri != null) {
                        imageList.add(imageUri)
                    }
                    val currImgHelp = currentImageSelect
                    val imageView = ImageView(this)
                    //create an add a custom imageview to a layout
                    imageView.layoutParams = RelativeLayout.LayoutParams(200, 200)
                    imageView.id = currentImageSelect
                    imageView.x = 10F
                    imageView.y = 10F
                    val layout = findViewById<LinearLayout>(R.id.imgviewcontainer)
                    layout.addView(imageView)
                    if (currImgHelp < 4) {
                        val imgInLayout = layout.getChildAt(currImgHelp + 1) as ImageView
                        imgInLayout.setImageURI(imageUri)
                    }
                    //adds a custom textview to the LinearLayout to show how much more images are selected
                    if (count > 4) {
                        val textView = findViewById<TextView>(R.id.imgcount)
                        val num = count - 4
                        textView.text = "+$num"
                    }
                    currentImageSelect += 1
                }
            }
            //Takes Image and adds it to an List that is used later also gives it to an Image View so it is visible for the user what image was selected
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

    private fun upload() {
        //Takes the list from onActivityResult and uploads the Images to Firestore
        val post: MutableMap<String, Any> = HashMap()
        val imageFolder = FirebaseStorage.getInstance().reference.child("ImageFolder")
        var uploads = 0
        //create an array with the uploaded image names
        while (uploads < imageList.size) {
            val uuid = UUID.randomUUID().toString()
            imgnames.add("gs://scapp-scouting.appspot.com/ImageFolder/image/$uuid")
            val imageName: StorageReference =
                imageFolder.child("image/$uuid")
            //upload image to Firestore
            imageName.putFile(imageList[uploads])
            uploads++
        }
        post["Img"] = imgnames

        //gets intent from MapsFragment with its LatLng additions
        val intent = intent
        //takes extras and merges them into a LatLng
        val latitudeUpload = intent.getStringExtra("latitude")
        val longitudeUpload = intent.getStringExtra("longitude")
        val latlng = "$latitudeUpload, $longitudeUpload"
        val position = latlng.split(",").toTypedArray()
        val latitude = position[0].toDouble()
        val longitude = position[1].toDouble()
        val location = LatLng(latitude, longitude)
        post["Coordinates"] = location  //adds coordinates to post

        //takes String from a textview and adds them to post
        var text = findViewById<EditText>(R.id.popup_window_text)
        val description = text.text.toString()
        post["Description"] = description

        //takes String from a textview and adds them to post
        text = findViewById(R.id.popup_window_title)
        val title = text.text.toString()
        post["Title"] = title

        //gets current userid and adds them to post
        val userid = auth.currentUser?.uid.toString()
        post["UserId"] = userid

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        //Upload Post to Firebase
        db.collection("Posts")
            .add(post)
            .addOnSuccessListener { }
            .addOnFailureListener { e -> Log.w("Error adding document", e) }
        finish()
    }

}



