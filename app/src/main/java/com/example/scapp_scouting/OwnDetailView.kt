package com.example.scapp_scouting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class OwnDetailView : AppCompatActivity() {
    //Variablen für die Datenbankanbindung und die Anzeigen
    private val db = Firebase.firestore
    private var postID: String = ""
    private var imgTokenList = mutableListOf<String>()
    private val scrollViewImageSize = 400
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_own_detail_view)
        postID = intent.getStringExtra("postID").toString()
        auth = FirebaseAuth.getInstance()

        createAndFillHorizontalScrollView()
        fillViews()
    }

    private fun createAndFillHorizontalScrollView() {
        db.collection("Posts")
            .document(postID)
            .get()
            .addOnSuccessListener { result ->
                try {
                    //Bildreferenzen aus Firebase ziehen und in einer Liste abspeichern
                    val tempImageList = result.data?.get("Img") as ArrayList<*>
                    for (i in tempImageList.size downTo 1) {
                        imgTokenList.add(tempImageList[i - 1].toString()!!.substring(32, 86))
                    }

                    val horizontalScrollView = HorizontalScrollView(this)
                    //setting height and width
                    val layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    horizontalScrollView.layoutParams = layoutParams

                    val linearLayout = LinearLayout(this)
                    //setting height and width
                    val linearParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    linearLayout.layoutParams = linearParams

                    //adding horizontal scroll view to the layout
                    horizontalScrollView.addView(linearLayout)

                    for (item: String in imgTokenList) {
                        val tempImage = ShapeableImageView(this)
                        //setting height and width
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
                        // drawable folder and setting it to imageview
                        linearLayout.addView(tempImage)

                        //Bilder anhand der URI den einzelnen ImageViews zuweisen
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
                    }

                    //accessing the relative layout where the scrollview will be active
                    val linearLayout1 =
                        findViewById<RelativeLayout>(R.id.ownHorizontalScrollViewContainer)
                    linearLayout1?.addView(horizontalScrollView)


                } catch (e: Exception) {
                    Log.e(
                        "Error",
                        "Daten konnten dem DetailView nicht hinzugefügt werden: $e"
                    )
                }
            }
    }

    private fun fillViews() {
        //Bilder laden
        db.collection("Posts")
            .document(postID)
            .get()
            .addOnSuccessListener { result ->
                try {
                    //Bild in HeaderImageView laden
                    val temp = result.data?.get("Img") as ArrayList<*>
                    var tempSnippet = temp[0] as String
                    var imgToken = tempSnippet!!.substring(32, 86)
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

                    //Titel in detailTitle laden
                    (findViewById<View>(R.id.ownDetailTitle) as TextView).text = result.data?.get("Title") as String

                    //Text in detailDescription laden
                    (findViewById<View>(R.id.ownDetailDescription) as TextView).text = result.data?.get("Description") as String

                    //Text in detailUserID laden
                    if(result.data?.get("UserId") != auth.currentUser?.uid){
                        (findViewById<View>(R.id.ownDetailUserID) as TextView).text = "User-ID des Erstellers: " + (result.data?.get("UserId") as String)
                    }else{
                        (findViewById<View>(R.id.ownDetailUserID) as TextView).text = "Dieser Post wurde von dir erstellt."
                    }

                } catch (e: Exception) {
                    Log.e(
                        "Error",
                        "Daten konnten dem DetailView nicht hinzugefügt werden: $e"
                    )
                }
            }

    }
}