package com.example.scapp_scouting

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.net.URL
import java.util.*
import javax.annotation.Nullable
import kotlin.collections.HashMap


class CreateMarker : AppCompatActivity() {


    var btnSelect: android.widget.Button? = findViewById(R.id.ImgButton);
    var btnUpload:android.widget.Button? = findViewById(R.id.popup_window_button);
    var imageView:android.widget.ImageView? = findViewById(R.id.imgView);
    var filePath: Uri? = null
    var GALLERY_REQ_CODE = 22
    var storage: FirebaseStorage? = FirebaseStorage.getInstance();
    var storageReference: StorageReference? = storage?.getReference();
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_marker);

        //ImgView = findViewById(R.id.imageView) as ImageView

        btnSelect?.setOnClickListener{

            SelectImage()

        }
        //val btn_click_me = findViewById(R.id.popup_window_button) as Button
        // set on-click listener
        btnUpload?.setOnClickListener {
            upload()
        }
    }
    private fun SelectImage() {

        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            GALLERY_REQ_CODE
        )
    }
    @Override
    protected override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode== RESULT_OK){
            if (resultCode== GALLERY_REQ_CODE){
                if (data != null) {
                    if (data.data != null) {
                        //ImgView.setImageURI(data.data)
                        filePath = data.data
                        try{
                            val bitmap = MediaStore.Images.Media
                                .getBitmap(
                                    contentResolver,
                                    filePath
                                )
                            imageView!!.setImageBitmap(bitmap)
                        }catch (ioe: IOException){
                           println(ioe.message)
                        }

                    }
                }
            }
        }
    }
    fun upload(){
        if (filePath != null){
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref = storageReference?.child(
                "images/" + UUID.randomUUID().toString()
            )

            if (ref != null) {
                ref.putFile(filePath!!)
            }

        }



        val intent = getIntent()
        val Post: MutableMap<String, Any> = HashMap()


        //val img = intent1
        //Post.put("Img", img)

        val latetude = intent.getStringExtra("latetude")
        val longetude = intent.getStringExtra("longetude")
        val latlong = latetude+","+longetude;
        val position = latlong.split(",").toTypedArray()
        val latitude = position[0].toDouble()
        val longitude = position[1].toDouble()
        val location = LatLng(latitude, longitude)
        Post.put("Coordinates", location);


        var text   = findViewById<EditText>(R.id.popup_window_text);
        val description = text.text.toString()
        Post.put("Description", description);



        Post.put("Img", "Turing");

        text   = findViewById<EditText>(R.id.popup_window_title);
        val title = text.text.toString()
        Post.put("Title", title)

        Post.put("UserId", 2);

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        db.collection("Posts")
            .add(Post)
            .addOnSuccessListener { documentReference ->

            }
            .addOnFailureListener { e -> Log.w( "Error adding document", e) }
    }
}


