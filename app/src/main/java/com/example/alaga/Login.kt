package com.example.alaga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DatabaseHelper(this)

        val nameInput = findViewById<EditText>(R.id.SIName)
        val passwordInput = findViewById<EditText>(R.id.SIPassword)
        val loginButton = findViewById<Button>(R.id.SILogin)

        val backSignin : Button = findViewById(R.id.SIBackButton)

        backSignin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val redirectSU : TextView = findViewById(R.id.SUredirect)
        redirectSU.setOnClickListener{
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = dbHelper.authenticateUser(name, password)

            if (role != null) {
                // Store session data
                val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("username", name)
                    putString("role", role)
                    apply()  // Using apply() for asynchronous save
                }

                Toast.makeText(this, "Login Successful as $role", Toast.LENGTH_SHORT).show()

                // Clear the back stack and launch homepage
                val intent = Intent(this, Homepage::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}