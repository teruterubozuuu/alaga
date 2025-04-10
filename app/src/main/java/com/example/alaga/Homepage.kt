package com.example.alaga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val currentUser = sharedPreferences.getString("username", null)
        val currentRole = sharedPreferences.getString("role", null)

        val usernameText = findViewById<TextView>(R.id.usernameText)
        usernameText.text = "$currentUser"
        val roleText = findViewById<TextView>(R.id.roleText)
        roleText.text = "$currentRole"

        setupButtons(currentRole ?: "Unknown")

        val logoutBtn : Button = findViewById(R.id.logout)
        logoutBtn.setOnClickListener{
            logout()
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Remove stored user data
        editor.apply()

        // Redirect to Login page & clear activity stack
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupButtons(role: String) {
        val layout = findViewById<LinearLayout>(R.id.buttonContainer)


        val roleButtons = when (role) {
            "Admin" -> listOf("Personal Details", "View Appointments", "User Account Module", "Patient Information Module")
            "Doctor" -> listOf("Personal Details","Patients List", "View Appointments")
            "Nurse" -> listOf("Personal Details", "Patients List", "View Appointments")
            "Patient" -> listOf("Personal Details", "Medical History", "View Appointments", "Patient Appointment System")
            else -> emptyList()
        }

        (roleButtons).forEach { buttonText ->
            val button = Button(this).apply {
                text = buttonText
                textSize = 16f
                setTextColor(resources.getColor(R.color.lightBlack, null))
                setBackgroundResource(R.drawable.homebtn)
                setPadding(20, 50, 20, 50)

                setOnClickListener {
                    when (buttonText) {
                        "Personal Details" -> startActivity(Intent(this@Homepage, PersonalDetails::class.java))
                        "User Account Module" -> startActivity(Intent(this@Homepage, UserAccountModule::class.java))
                        "Patient Information Module" -> startActivity(Intent(this@Homepage, PatientAccountModule::class.java))
                        "Medical History" -> {
                            val intent = Intent(this@Homepage, PatientMedHistory::class.java)
                            intent.putExtra("username", getSharedPreferences("UserSession", MODE_PRIVATE).getString("username", ""))
                            startActivity(intent)
                        }
                        "Patients List" -> {
                            val intent = Intent(this@Homepage, DoctorMedHistoryList::class.java)
                            startActivity(intent)
                        }
                        "Patient Appointment System" -> startActivity(Intent(this@Homepage, PatientAppointment::class.java))
                        "View Appointments" -> startActivity(Intent(this@Homepage, AppointmentHistory::class.java))
                    }
                }
            }
            // Set margin using LayoutParams
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,  // Button width
                LinearLayout.LayoutParams.WRAP_CONTENT   // Button height
            ).apply {
                setMargins(20, 25, 20, 25) // Left, Top, Right, Bottom margin
            }

            button.layoutParams = params
            layout.addView(button)
        }
    }
}