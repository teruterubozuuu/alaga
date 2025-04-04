package com.example.alaga

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PatientAppointment : AppCompatActivity() {
    private lateinit var doctorSpinner: Spinner
    private lateinit var dateEditText: EditText
    private lateinit var bookButton: Button
    private lateinit var backButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private var patientId: Int = -1
    private var patientName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)

        patientName = sharedPreferences.getString("username", "") ?: ""
        patientId = sharedPreferences.getInt("userId", -1)

        if (patientId == -1 && patientName.isNotEmpty()) {
            patientId = dbHelper.getUserIdByUsername(patientName)
            if (patientId != -1) {
                sharedPreferences.edit().putInt("userId", patientId).apply()
            }
        }

        doctorSpinner = findViewById(R.id.doctorSpinner)
        dateEditText = findViewById(R.id.dateEditText)
        bookButton = findViewById(R.id.bookAppointmentButton)
        backButton = findViewById(R.id.backButton)

        loadDoctors()

        dateEditText.setOnClickListener {
            showDatePicker()
        }

        bookButton.setOnClickListener {
            bookAppointment()
        }

        backButton.setOnClickListener {
            finish()
        }


    }

    private fun loadDoctors() {
        val doctors = dbHelper.getAllUsers("Doctor")
        if (doctors.isEmpty()) {
            Toast.makeText(this, "No doctors available", Toast.LENGTH_SHORT).show()
            return
        }

        val doctorNames = doctors.map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctorNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        doctorSpinner.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                dateEditText.setText(dateFormat.format(selectedDate.time))
            },
            year, month, day
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun bookAppointment() {
        try {
            val selectedDoctorName = doctorSpinner.selectedItem as? String ?: run {
                Toast.makeText(this, "Please select a doctor", Toast.LENGTH_SHORT).show()
                return
            }

            val selectedDate = dateEditText.text.toString().takeIf { it.isNotBlank() } ?: run {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return
            }

            // Debug log
            Log.d("Appointment", "Attempting to book appointment for patientId: $patientId")

            if (patientId == -1) {
                Toast.makeText(this, "Patient not recognized", Toast.LENGTH_SHORT).show()
                return
            }

            val doctorId = dbHelper.getUserIdByUsername(selectedDoctorName).takeIf { it != -1 } ?: run {
                Toast.makeText(this, "Doctor not found", Toast.LENGTH_SHORT).show()
                return
            }

            // Debug log
            Log.d("Appointment", "Checking for existing appointments...")

            if (dbHelper.hasAppointment(patientId, doctorId, selectedDate)) {
                Toast.makeText(
                    this,
                    "You already have an appointment with this doctor on $selectedDate",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            // Debug log
            Log.d("Appointment", "Attempting to book new appointment...")

            if (dbHelper.bookAppointment(patientId, doctorId, selectedDate)) {
                Toast.makeText(
                    this,
                    "Appointment booked with Dr. $selectedDoctorName on $selectedDate",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Failed to book appointment. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("Appointment", "Error booking appointment", e)
            Toast.makeText(this, "Error booking appointment: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}