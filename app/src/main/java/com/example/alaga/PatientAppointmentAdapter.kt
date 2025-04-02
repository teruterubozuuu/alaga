package com.example.alaga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alaga.R
import com.example.alaga.models.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class PatientAppointmentAdapter(
    private val context: Context,
    private val appointments: List<Appointment>,
    private val onItemClick: (Appointment) -> Unit
) : RecyclerView.Adapter<PatientAppointmentAdapter.AppointmentViewHolder>() {

    private val dbHelper: DatabaseHelper by lazy { DatabaseHelper(context) }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientName: TextView = itemView.findViewById(R.id.patientNameTextView)
        val doctorName: TextView = itemView.findViewById(R.id.doctorNameTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_patient, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        val patientName = dbHelper.getUserNameById(appointment.patientId)
        val doctorName = dbHelper.getUserNameById(appointment.doctorId)

        holder.patientName.text = "Patient: $patientName"
        holder.doctorName.text = "Doctor: $doctorName"
        // OR if you want IDs instead:
        // holder.patientName.text = "Patient ID: ${appointment.patientId}"
        // holder.doctorName.text = "Doctor ID: ${appointment.doctorId}"

        // Changes
        val formattedDate = try {
            val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(appointment.appointmentDate)
            outputFormat.format(date)
        } catch (e: Exception) {
            appointment.appointmentDate
        }

        holder.date.text = formattedDate
        holder.status.text = appointment.status

        holder.itemView.setOnClickListener {
            onItemClick(appointment)
        }
    }

    override fun getItemCount() = appointments.size
}