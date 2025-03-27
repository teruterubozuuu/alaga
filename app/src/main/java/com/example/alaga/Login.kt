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
            val name = nameInput.text.toString()
            val password = passwordInput.text.toString()

            val role = dbHelper.authenticateUser(name, password)

            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("username", name)
            editor.putString("role", role)
            editor.apply()

            if (role != null) {
                Toast.makeText(this, "Login Successful as $name", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,  Homepage::class.java))
            } else {
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }


        }
    }
}