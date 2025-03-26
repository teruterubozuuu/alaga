package com.example.alaga

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DatabaseHelper(this)

        val backSignup : Button = findViewById(R.id.SUBackButton)
        val nameInput = findViewById<EditText>(R.id.SUname)
        val emailInput = findViewById<EditText>(R.id.SUemail)
        val passwordInput = findViewById<EditText>(R.id.SUpassword)
        val roleSpinner = findViewById<Spinner>(R.id.SUrole)
        val regBTN = findViewById<Button>(R.id.signUpBtn)

        val roles = arrayOf("Admin", "Doctor", "Nurse", "Patient")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        roleSpinner.adapter = adapter

        backSignup.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val signIn : TextView = findViewById(R.id.SIredirect)
        signIn.setOnClickListener{
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        regBTN.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val selectedRole = roleSpinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // uses DataBase helper
            val success = dbHelper.insertUser(name, email, password, selectedRole)

            if (success) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
                dbHelper.printAllUsers()
                finish()
            } else {
                Toast.makeText(this, "Error: Email might be taken", Toast.LENGTH_SHORT).show()
            }
        }
    }
}