package com.example.alaga

import com.example.alaga.R
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Locale


class PatientMedHistory : AppCompatActivity() {

    private lateinit var birthdateEditText: EditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_med_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        birthdateEditText = findViewById(R.id.MHbirthdate)

        birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        val backButton : Button = findViewById(R.id.backButton)

        backButton.setOnClickListener{
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        val patientName = findViewById<EditText>(R.id.MHpatientName)
        val age = findViewById<EditText>(R.id.MHage)
        val sex = findViewById<EditText>(R.id.MHsex)
        val birthdate = findViewById<EditText>(R.id.MHbirthdate)
        val phoneNumber = findViewById<EditText>(R.id.MHphoneNumber)
        val emergencyNumber = findViewById<EditText>(R.id.MHemergency)
        val marital = findViewById<EditText>(R.id.MHmarital)
        val occupation = findViewById<EditText>(R.id.MHoccupation)
        val chkboxA1 = findViewById<CheckBox>(R.id.A1)
        val chkboxA2 = findViewById<CheckBox>(R.id.A2)
        val chkboxB1 = findViewById<CheckBox>(R.id.B1)
        val chkboxB2 = findViewById<CheckBox>(R.id.B2)
        val chkboxC1 = findViewById<CheckBox>(R.id.C1)
        val chkboxC2 = findViewById<CheckBox>(R.id.C2)
        val chkboxD1 = findViewById<CheckBox>(R.id.D1)
        val chkboxD2 = findViewById<CheckBox>(R.id.D2)
        val chkboxE1 = findViewById<CheckBox>(R.id.E1)
        val chkboxE2 = findViewById<CheckBox>(R.id.E2)
        val otherIssues = findViewById<EditText>(R.id.MHotherIssues)
        val treatmentName = findViewById<EditText>(R.id.MHtreatmentName)
        val treatmentDosage = findViewById<EditText>(R.id.MHtreatmentDosage)
        val treatmentFrequency = findViewById<EditText>(R.id.MHtreatmentFrequency)
        val treatmentPurpose = findViewById<EditText>(R.id.MHtreatmentPurpose)
        val treatmentNotes = findViewById<EditText>(R.id.MHtreatmentNotes)

        val saveButton = findViewById<Button>(R.id.MHSaveBtn)
        saveButton.setOnClickListener{
            val patientName = patientName.text.toString().trim()
            val age = age.text.toString().trim()
            val sex = sex.text.toString().trim()
            val birthdate = birthdate.text.toString().trim()
            val phoneNumber = phoneNumber.text.toString().trim()
            val emergencyNumber = emergencyNumber.text.toString().trim()
            val marital = marital.text.toString().trim()
            val occupation = occupation.text.toString().trim()

        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                birthdateEditText.setText(dateFormat.format(calendar.time))
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}