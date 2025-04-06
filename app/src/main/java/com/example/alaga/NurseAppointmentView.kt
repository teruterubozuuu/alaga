package com.example.alaga

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alaga.DatabaseHelper
import com.example.alaga.R
import com.example.alaga.models.Appointment
import com.example.alaga.adapters.AppointmentAdapter

class NurseAppointmentView : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
    private var appointmentList = mutableListOf<Appointment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_appointment_view)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
            finish()
        }


        loadAppointments()
    }

    private fun loadAppointments() {
        appointmentList = dbHelper.getAllAppointments().toMutableList()
        adapter = AppointmentAdapter(appointmentList) { appointment ->
            updateAppointmentStatus(appointment)
        }
        recyclerView.adapter = adapter
    }

    private fun updateAppointmentStatus(appointment: Appointment) {
        if (appointment.status == "Scheduled") {
            Toast.makeText(this, "Appointment is already scheduled!", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.updateAppointmentStatus(appointment.id, "Scheduled")
        if (true) {
            appointment.status = "Scheduled"
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Appointment updated to Scheduled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update appointment!", Toast.LENGTH_SHORT).show()
        }
    }
}