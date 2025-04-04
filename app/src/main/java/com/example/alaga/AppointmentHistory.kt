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
import com.example.alaga.models.Appointment

class AppointmentHistory : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView

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
        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("userId", -1)
        val currentUserRole = sharedPreferences.getString("role", "") ?: ""

        if (currentUserId == -1) {
            Toast.makeText(this, "User not recognized", Toast.LENGTH_SHORT).show()
            return
        }

        val appointments = if (currentUserRole == "Patient") {
            dbHelper.getAllAppointments().filter { it.patientId == currentUserId }
        } else if (currentUserRole == "Doctor") {
            dbHelper.getAllAppointments().filter { it.doctorId == currentUserId }
        } else {
            dbHelper.getAllAppointments()
        }

        if (appointments.isEmpty()) {
            Toast.makeText(this, "No appointments found", Toast.LENGTH_SHORT).show()
        }

        val adapter = PatientAppointmentAdapter(
            context = this,
            appointments = appointments,
            userRole = currentUserRole,
            onCancel = { appointment ->
                cancelAppointment(appointment)
            },
            onReschedule = { appointment ->
                rescheduleAppointment(appointment)
            },
            onAccept = { appointment ->
                acceptAppointment(appointment)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun acceptAppointment(appointment: Appointment) {
        dbHelper.updateAppointmentStatus(appointment.id, "Accepted")
        Toast.makeText(this, "Appointment accepted", Toast.LENGTH_SHORT).show()
        loadAppointments()
    }

    private fun cancelAppointment(appointment: Appointment) {
        dbHelper.updateAppointmentStatus(appointment.id, "Cancelled")
        Toast.makeText(this, "Appointment cancelled", Toast.LENGTH_SHORT).show()
        loadAppointments()
    }

    private fun rescheduleAppointment(appointment: Appointment) {
        val intent = Intent(this, RescheduleActivity::class.java).apply {
            putExtra("APPOINTMENT_ID", appointment.id)
            putExtra("CURRENT_DATE", appointment.appointmentDate)
            putExtra("DOCTOR_ID", appointment.doctorId)
        }
        startActivity(intent)
    }
}