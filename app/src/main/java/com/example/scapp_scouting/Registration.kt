package com.example.scapp_scouting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Registration : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var passwordconfirm: EditText
    private lateinit var password: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnRedirectLogin: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        //Get Buttons and TextViews from xml
        email = findViewById(R.id.email)
        passwordconfirm = findViewById(R.id.passwordconf)
        password = findViewById(R.id.password)
        btnSignUp = findViewById(R.id.register)
        btnRedirectLogin = findViewById(R.id.alreadyhasaccount)
        auth = Firebase.auth

        //creates onclicklistener which starts signeUpUser when activated
        btnSignUp.setOnClickListener {
            signUpUser()
        }
        //creates onclicklistener which redirects user to Login when activated
        btnRedirectLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    //Takes User input and creates a new user in Firebase
    private fun signUpUser() {
        val email = email.text.toString()
        val password = password.text.toString()
        val confirmPassword = passwordconfirm.text.toString()

        //check if user input is valid
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Password and confirm-password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        //starts activity Login if user creation successful
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully singed up", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Singed up failed! perhaps try again (with numbers in password etc.)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}