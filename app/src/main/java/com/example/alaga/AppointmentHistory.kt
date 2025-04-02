package com.example.alaga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alaga.adapters.AppointmentAdapter
import com.example.alaga.models.Appointment

class AppointmentHistory : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var appointmentList: MutableList<Appointment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_appointment_history)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewAppointmentHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
            finish()
        }

       loadAppointments()

    }

    private fun loadAppointments() {
        // Get the current user's ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("userId", -1)
        val currentUserRole = sharedPreferences.getString("role", "") ?: ""

        if (currentUserId == -1) {
            Toast.makeText(this, "User not recognized", Toast.LENGTH_SHORT).show()
            return
        }

        // Get appointments based on user role
        val appointments = if (currentUserRole == "Patient") {
            // For patients, show only their appointments
            dbHelper.getAllAppointments().filter { it.patientId == currentUserId }
        } else if (currentUserRole == "Doctor") {
            // For doctors, show only their appointments
            dbHelper.getAllAppointments().filter { it.doctorId == currentUserId }
        } else {
            // For admin/nurse, show all appointments
            dbHelper.getAllAppointments()
        }

        if (appointments.isEmpty()) {
            Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show()
        }

        // Create adapter and set it to RecyclerView
        val adapter = PatientAppointmentAdapter(
            context = this,  // Pass the activity context
            appointments = appointments,
            onItemClick = { appointment ->
                // Handle item click if needed
                Toast.makeText(this, "Clicked: ${appointment.id}", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }
}