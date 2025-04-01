package com.example.alaga

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PatientDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)

        val patientName = intent.getStringExtra("patientName")
        val age = intent.getIntExtra("age", 0)
        val sex = intent.getStringExtra("sex")
        val birthdate = intent.getStringExtra("birthdate")
        val phone = intent.getStringExtra("phone")

        findViewById<TextView>(R.id.detailsPatientName).text = "Name: $patientName"
        findViewById<TextView>(R.id.detailsAge).text = "Age: $age"
        findViewById<TextView>(R.id.detailsSex).text = "Sex: $sex"
        findViewById<TextView>(R.id.detailsBirthdate).text = "Birthdate: $birthdate"
        findViewById<TextView>(R.id.detailsPhone).text = "Phone: $phone"
    }
}
