package com.example.alaga

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.BaseAdapter

class PatientListAdapter(private val context: Context, private val patientList: List<DatabaseHelper.MedicalHistory>) : BaseAdapter() {

    override fun getCount(): Int = patientList.size

    override fun getItem(position: Int): Any = patientList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_patient, parent, false)
        val patient = patientList[position]

        val patientNameTextView = view.findViewById<TextView>(R.id.patientNameTextView)
        val viewButton = view.findViewById<Button>(R.id.viewPatientButton)

        patientNameTextView.text = patient.patientName

        viewButton.setOnClickListener {
            val intent = Intent(context, PatientDetailsActivity::class.java).apply {
                putExtra("patientName", patient.patientName)
                putExtra("age", patient.age)
                putExtra("sex", patient.sex)
                putExtra("birthdate", patient.birthdate)
                putExtra("phone", patient.phone)
            }
            context.startActivity(intent)
        }

        return view
    }
}
