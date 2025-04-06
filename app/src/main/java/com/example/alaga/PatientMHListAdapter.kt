package com.example.alaga

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientMHListAdapter(
    private val context: Context,
    private val patients: List<DatabaseHelper.MedicalHistory>
) : RecyclerView.Adapter<PatientMHListAdapter.PatientViewHolder>() {

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView = itemView.findViewById(R.id.patientNameTextView)
        val viewHistoryBtn: Button = itemView.findViewById(R.id.viewPatientButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_patient_mh_list, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.patientName.text = patient.patientName


    }

    override fun getItemCount() = patients.size
}