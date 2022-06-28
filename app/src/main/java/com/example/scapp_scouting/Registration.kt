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
    private lateinit var Email: EditText
    private lateinit var passwordconfirm: EditText
    private lateinit var Password: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnRedirectLogin: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        Email = findViewById(R.id.email)
        passwordconfirm = findViewById(R.id.passwordconf)
        Password = findViewById(R.id.password)
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
        val email = Email.text.toString()
        val password = Password.text.toString()
        val confirmpassword = passwordconfirm.text.toString()

        if (email.isBlank() || password.isBlank() || confirmpassword.isBlank()) {
            Toast.makeText(this, "email and password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmpassword) {
            Toast.makeText(this, "password and confirm-password do not match", Toast.LENGTH_SHORT)
                .show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "successfully singed up", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "singed up failed! perhaps try again (with numbers in password etc.)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}