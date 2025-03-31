package com.example.alaga

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.TextView


class PersonalDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_details)

        // Get user session data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val name = sharedPreferences.getString("username", "Unknown")
        val email = sharedPreferences.getString("email", "Unknown")
        val role = sharedPreferences.getString("role", "Unknown")

        // Display user details
        findViewById<TextView>(R.id.nameTextView).text = "Name: $name"
        findViewById<TextView>(R.id.emailTextView).text = "Email: $email"
        findViewById<TextView>(R.id.roleTextView).text = "Role: $role"

        // Back button to return to Homepage
        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
            finish()
        }
    }
}