package com.example.alaga

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RescheduleActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var dateEditText: EditText
    private var appointmentId: Int = -1
    private var doctorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reschedule)

        dbHelper = DatabaseHelper(this)
        dateEditText = findViewById(R.id.dateEditText)

        appointmentId = intent.getIntExtra("APPOINTMENT_ID", -1)
        doctorId = intent.getIntExtra("DOCTOR_ID", -1)
        val currentDate = intent.getStringExtra("CURRENT_DATE")

        dateEditText.setText(currentDate)

        dateEditText.setOnClickListener {
            showDatePicker()
        }

        findViewById<Button>(R.id.rescheduleButton).setOnClickListener {
            rescheduleAppointment()
        }


    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
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

    private fun rescheduleAppointment() {
        val newDate = dateEditText.text.toString()
        if (newDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        if (!dbHelper.isDoctorAvailable(doctorId, newDate)) {
            Toast.makeText(this, "Doctor is not available on this date", Toast.LENGTH_SHORT).show()
            return
        }

        if (dbHelper.updateAppointmentDate(appointmentId, newDate)) {
            Toast.makeText(this, "Appointment rescheduled", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to reschedule", Toast.LENGTH_SHORT).show()
        }
    }
}