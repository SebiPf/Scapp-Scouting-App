package com.example.scapp_scouting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
    private lateinit var btnRedirectLogin: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        email = findViewById(R.id.email)
        passwordconfirm = findViewById(R.id.passwordconf)
        password = findViewById(R.id.password)
        btnSignUp = findViewById(R.id.register)
        btnRedirectLogin = findViewById(R.id.alreadyhasaccount)
        auth = Firebase.auth

        btnSignUp.setOnClickListener {
            signUpUser()
        }
        btnRedirectLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    private fun signUpUser() {
        val email = email.text.toString()
        val password = password.text.toString()
        val confirmPassword = passwordconfirm.text.toString()

        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Password and confirm-password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully singed up", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Singed up failed! perhaps try again (with numbers in password etc.)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}