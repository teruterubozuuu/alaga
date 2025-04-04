package com.example.alaga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DoctorMedHistoryList : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_doctor_med_history)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewMedHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
            finish()
        }

        loadPatients()
    }

    private fun loadPatients() {
        val patients = dbHelper.getAllPatients()
        val adapter = PatientMHListAdapter(this, patients)
        recyclerView.adapter = adapter
    }
}