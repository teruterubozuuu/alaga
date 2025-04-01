package com.example.alaga

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteException
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PatientMedHistory : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var birthdateEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_med_history)


        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        dbHelper = DatabaseHelper(this)
        birthdateEditText = findViewById(R.id.MHbirthdate)

        val intentUsername = intent.getStringExtra("username")
        if (intentUsername != null) {
            sharedPreferences.edit().putString("username", intentUsername).apply()
        }

        // Set up Spinners
        setupSpinners()

        birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, Homepage::class.java))
        }

        findViewById<Button>(R.id.MHSaveBtn).setOnClickListener {
            saveMedicalHistory()
        }

        // Load existing medical history if it exists
        fetchAndDisplayMedicalHistory()


    }

    private fun saveMedicalHistory() {
        // Get current username from SharedPreferences
        val username = sharedPreferences.getString("username", null)
        if (username.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            logout()
            return
        }

        // Get all input values
        val patientName = findViewById<EditText>(R.id.MHpatientName).text.toString().trim()
        val ageText = findViewById<EditText>(R.id.MHage).text.toString().trim()
        val age = ageText.toIntOrNull() ?: 0
        val selectedSex = findViewById<Spinner>(R.id.MHsex).selectedItem.toString()
        val birthdate = findViewById<EditText>(R.id.MHbirthdate).text.toString().trim()
        val phoneNumber = findViewById<EditText>(R.id.MHphoneNumber).text.toString().trim()
        val emergencyNumber = findViewById<EditText>(R.id.MHemergency).text.toString().trim()
        val occupation = findViewById<EditText>(R.id.MHoccupation).text.toString().trim()

        // Collect all checked medical issues
        val medicalIssues = mutableListOf<String>()
        val checkboxes = listOf(
            R.id.A1, R.id.A2, R.id.B1, R.id.B2,
            R.id.C1, R.id.C2, R.id.D1, R.id.D2,
            R.id.E1, R.id.E2
        )

        for (checkboxId in checkboxes) {
            val checkBox = findViewById<CheckBox>(checkboxId)
            if (checkBox.isChecked) medicalIssues.add(checkBox.text.toString())
        }

        // Add other issues if specified
        val otherIssuesText = findViewById<EditText>(R.id.MHotherIssues).text.toString().trim()
        val allIssues = (medicalIssues + otherIssuesText).joinToString(", ")

        // Get treatment information
        val treatmentName = findViewById<EditText>(R.id.MHtreatmentName).text.toString().trim()
        val treatmentDosage = findViewById<EditText>(R.id.MHtreatmentDosage).text.toString().trim()
        val treatmentFrequency = findViewById<EditText>(R.id.MHtreatmentFrequency).text.toString().trim()
        val treatmentPurpose = findViewById<EditText>(R.id.MHtreatmentPurpose).text.toString().trim()
        val treatmentNotes = findViewById<EditText>(R.id.MHtreatmentNotes).text.toString().trim()

        // Save to database
        val success = dbHelper.insertMedicalHistoryByUsername(
            username,
            patientName,
            age,
            selectedSex,
            birthdate,
            phoneNumber,
            emergencyNumber,
            occupation,
            allIssues,
            treatmentName,
            treatmentDosage,
            treatmentFrequency,
            treatmentPurpose,
            treatmentNotes
        )

        if (success) {
            Toast.makeText(this, "Medical history saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to save medical history", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchAndDisplayMedicalHistory() {
        val username = sharedPreferences.getString("username", null)
        if (username.isNullOrEmpty()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
            logout()
            return
        }



        try{
            val history = dbHelper.getMedicalHistoryByUsername(username)
            if (history != null) {
                // Populate patient information
                findViewById<EditText>(R.id.MHpatientName).setText(history.patientName)
                findViewById<EditText>(R.id.MHage).setText(history.age.toString())

                // Set sex spinner selection
                val sexSpinner = findViewById<Spinner>(R.id.MHsex)
                val sexAdapter = sexSpinner.adapter as ArrayAdapter<String>
                val sexPosition = sexAdapter.getPosition(history.sex)
                if (sexPosition >= 0) {
                    sexSpinner.setSelection(sexPosition)
                }

                findViewById<EditText>(R.id.MHbirthdate).setText(history.birthdate)
                findViewById<EditText>(R.id.MHphoneNumber).setText(history.phone)
                findViewById<EditText>(R.id.MHemergency).setText(history.emergency)
                findViewById<EditText>(R.id.MHoccupation).setText(history.occupation)

                // Set checkboxes based on saved issues
                if (!history.issues.isNullOrEmpty()) {
                    val issuesList = history.issues.split(", ")
                    val checkboxes = listOf(R.id.A1, R.id.A2, R.id.B1, R.id.B2, R.id.C1, R.id.C2, R.id.D1, R.id.D2, R.id.E1, R.id.E2)

                    checkboxes.forEach { checkboxId ->
                        val checkBox = findViewById<CheckBox>(checkboxId)
                        checkBox.isChecked = issuesList.contains(checkBox.text.toString())
                    }

                    // Check if there are other issues not covered by checkboxes
                    val predefinedIssues = checkboxes.map { findViewById<CheckBox>(it).text.toString() }
                    val otherIssues = issuesList.filter { !predefinedIssues.contains(it) }
                    if (otherIssues.isNotEmpty()) {
                        findViewById<EditText>(R.id.MHotherIssues).setText(otherIssues.joinToString(", "))
                    }
                }

                // Populate treatment information
                findViewById<EditText>(R.id.MHtreatmentName).setText(history.treatment)
                findViewById<EditText>(R.id.MHtreatmentDosage).setText(history.dosage)
                findViewById<EditText>(R.id.MHtreatmentFrequency).setText(history.frequency)
                findViewById<EditText>(R.id.MHtreatmentPurpose).setText(history.purpose)
                findViewById<EditText>(R.id.MHtreatmentNotes).setText(history.notes)
            }
        }catch (e: SQLiteException) {
            Log.e("PatientMedHistory", "Error accessing medical history", e)
            // Handle case where table doesn't exist yet
            Toast.makeText(this, "No medical history found", Toast.LENGTH_SHORT).show()
        }

    }

    private fun logout() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()  // Prevents user from going back
    }

    private fun setupSpinners() {
        val sexSpinner = findViewById<Spinner>(R.id.MHsex)
        val maritalStatusSpinner = findViewById<Spinner>(R.id.MHmarital)

        val sexOptions = arrayOf("Male", "Female", "Other")
        val maritalOptions = arrayOf("Single", "Married", "Divorced", "Widowed")

        val sexAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sexOptions)
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sexSpinner.adapter = sexAdapter

        val maritalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, maritalOptions)
        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        maritalStatusSpinner.adapter = maritalAdapter
    }



    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                birthdateEditText.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time))
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
