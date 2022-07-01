package com.example.scapp_scouting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var btnRedirectSignUp: Button
    private lateinit var loginemail: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Get Buttons and TextViews from xml
        btnRedirectSignUp = findViewById(R.id.Registerlogin)
        btnLogin = findViewById(R.id.LoginLogin)
        loginemail = findViewById(R.id.Emaillogin)
        password = findViewById(R.id.PasswordLogin)
        auth = FirebaseAuth.getInstance()

        //get current user
        val user = Firebase.auth.currentUser

        //checks i user is still logged in if so starts MainActivity
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        //creates onclicklistener which starts login when activated
        btnLogin.setOnClickListener {
            login()
        }

        //creates onclicklistener which redirects user to Registration when activated
        btnRedirectSignUp.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish()
        }
    }

    //Takes user input and signe in the user
    private fun login() {
        val email = loginemail.text.toString()
        val pass = password.text.toString()
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                //creates message for user to see if login worked or not
                Toast.makeText(this, "successfully logged in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else
                Toast.makeText(this, "login failed ", Toast.LENGTH_SHORT).show()
        }
    }
}