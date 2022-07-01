package com.example.scapp_scouting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

//Reason for OwnDetailView instead of using ListDetailView:
//Coming functions to edit own locations

class OwnDetailView : AppCompatActivity() {
    //Variables for the database connection and the displays
    private val db = Firebase.firestore
    private var postID: String = ""
    private var imgTokenList = mutableListOf<String>()
    private val scrollViewImageSize = 400
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_own_detail_view)

        //Get Variable and Instance for Firebase-Authentification
        postID = intent.getStringExtra("postID").toString()
        auth = FirebaseAuth.getInstance()

        //Fill views
        createAndFillHorizontalScrollView()
        fillViews()
    }

    private fun createAndFillHorizontalScrollView() {
        db.collection("Posts")
            .document(postID)
            .get()
            .addOnSuccessListener { result ->
                try {
                    //Drag image references from Firebase and save them in a list
                    val tempImageList = result.data?.get("Img") as ArrayList<*>
                    for (i in tempImageList.size downTo 1) {
                        imgTokenList.add(tempImageList[i - 1].toString().substring(32, 86))
                    }

                    val horizontalScrollView = HorizontalScrollView(this)
                    //Setting height and width
                    val layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    horizontalScrollView.layoutParams = layoutParams

                    val linearLayout = LinearLayout(this)
                    //Setting height and width
                    val linearParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    linearLayout.layoutParams = linearParams

                    //Adding horizontal scroll view to the layout
                    horizontalScrollView.addView(linearLayout)

                    for (item: String in imgTokenList) {
                        val tempImage = ShapeableImageView(this)
                        //Setting height and width
                        val params1 = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        tempImage.layoutParams = params1
                        tempImage.layoutParams.height = scrollViewImageSize
                        tempImage.layoutParams.width = scrollViewImageSize

                        val radius = resources.getDimension(R.dimen.corner_radius)
                        val shapeAppearanceModel = tempImage.shapeAppearanceModel.toBuilder()
                            .setAllCornerSizes(radius)
                            .build()
                        tempImage.shapeAppearanceModel = shapeAppearanceModel

                        (tempImage.layoutParams as LinearLayout.LayoutParams).setMargins(
                            0,
                            0,
                            50,
                            0
                        )
                        //Drawable folder and setting it to imageview
                        linearLayout.addView(tempImage)

                        //Assigning images to individual ImageViews based on the URI
                        FirebaseStorage.getInstance().reference.child(item).downloadUrl.addOnSuccessListener {
                            var uri = it
                            val options: RequestOptions = RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.placeholder_01)
                                .error(R.drawable.placeholder_02)
                            Glide.with(this)
                                .load(uri)
                                .apply(options)
                                .into(tempImage)
                        }

                        //Add Fullscreen-View when click on Imageview
                        tempImage.setOnClickListener {
                            try {
                                val intent =
                                    Intent(this, FullscreenImageView::class.java).apply {
                                        putExtra("imgToken", item)
                                    }
                                ContextCompat.startActivity(this, intent, null)
                            } catch (e: Exception) {
                            }
                        }
                    }

                    //Accessing the relative layout where the scrollview will be active
                    val linearLayout1 =
                        findViewById<RelativeLayout>(R.id.ownHorizontalScrollViewContainer)
                    linearLayout1?.addView(horizontalScrollView)


                } catch (e: Exception) {
                    Log.e(
                        "Error",
                        "Data could not be added to the DetailView: $e"
                    )
                }
            }
    }

    private fun fillViews() {
        //Loading Images
        db.collection("Posts")
            .document(postID)
            .get()
            .addOnSuccessListener { result ->
                try {
                    //Load image into HeaderImageView
                    val temp = result.data?.get("Img") as ArrayList<*>
                    var tempSnippet = temp[0] as String
                    var imgToken = tempSnippet.substring(32, 86)
                    FirebaseStorage.getInstance().reference.child(imgToken).downloadUrl.addOnSuccessListener {
                        var uri = it
                        val options: RequestOptions = RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.placeholder_01)
                            .error(R.drawable.placeholder_02)
                        Glide.with(this)
                            .load(uri)
                            .apply(options)
                            .into(findViewById<View>(R.id.ownDetailHeaderImage) as ImageView)
                    }

                    //Load title in detailTitle
                    (findViewById<View>(R.id.ownDetailTitle) as TextView).text =
                        result.data?.get("Title") as String

                    //Load text in detailDescription
                    (findViewById<View>(R.id.ownDetailDescription) as TextView).text =
                        result.data?.get("Description") as String

                    //Load text in detailUserID
                    if (result.data?.get("UserId") != auth.currentUser?.uid) {
                        (findViewById<View>(R.id.ownDetailUserID) as TextView).text =
                            "User-ID des Erstellers: " + (result.data?.get("UserId") as String)
                    } else {
                        (findViewById<View>(R.id.ownDetailUserID) as TextView).text =
                            "Dieser Post wurde von dir erstellt."
                    }

                } catch (e: Exception) {
                    Log.e(
                        "Error",
                        "Data could not be added to the DetailView: $e"
                    )
                }
            }

    }
}