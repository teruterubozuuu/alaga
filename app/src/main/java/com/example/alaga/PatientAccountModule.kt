package com.example.alaga

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.ListView


class PatientAccountModule : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var patientListAdapter: PatientListAdapter
    private lateinit var patientListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_patient_account_module)

        dbHelper = DatabaseHelper(this)
        patientListView = findViewById(R.id.patientListView)

        val patientList = dbHelper.getAllPatients()
        patientListAdapter = PatientListAdapter(this, patientList)
        patientListView.adapter = patientListAdapter

        findViewById<Button>(R.id.backButton).setOnClickListener {
            startActivity(Intent(this, Homepage::class.java))
        }


    }
}