package com.example.scapp_scouting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var btnRedirectSignUp: TextView
    private lateinit var loginemail: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnRedirectSignUp = findViewById(R.id.Registerlogin)
        btnLogin = findViewById(R.id.LoginLogin)
        loginemail = findViewById(R.id.Emaillogin)
        password = findViewById(R.id.PasswordLogin)
        auth = FirebaseAuth.getInstance()

        val user = Firebase.auth.currentUser

        if(user != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            }

        btnLogin.setOnClickListener {
                login()
        }
        btnRedirectSignUp.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun login() {
        val email = loginemail.text.toString()
        val pass = password.text.toString()
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "successfully logged in", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else
                Toast.makeText(this, "login failed ", Toast.LENGTH_SHORT).show()
        }
    }
}