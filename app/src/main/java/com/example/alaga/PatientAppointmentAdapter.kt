package com.example.alaga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.alaga.R
import com.example.alaga.models.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class PatientAppointmentAdapter(
    private val context: Context,
    private val appointments: List<Appointment>,
    private val onCancel: (Appointment) -> Unit,
    private val onReschedule: (Appointment) -> Unit
) : RecyclerView.Adapter<PatientAppointmentAdapter.AppointmentViewHolder>() {

    private val dbHelper: DatabaseHelper by lazy { DatabaseHelper(context) }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName: TextView = itemView.findViewById(R.id.doctorNameTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
        val cancelBtn: Button = itemView.findViewById(R.id.cancelButton)
        val rescheduleBtn: Button = itemView.findViewById(R.id.rescheduleButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_patient, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        val doctorName = dbHelper.getUserNameById(appointment.doctorId)

        holder.doctorName.text = "Doctor: $doctorName"

        // Format the date
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

        // Only show buttons for pending appointments
        if (appointment.status == "Pending") {
            holder.cancelBtn.visibility = View.VISIBLE
            holder.rescheduleBtn.visibility = View.VISIBLE
        } else {
            holder.cancelBtn.visibility = View.GONE
            holder.rescheduleBtn.visibility = View.GONE
        }

        holder.cancelBtn.setOnClickListener {
            onCancel(appointment)
        }

        holder.rescheduleBtn.setOnClickListener {
            onReschedule(appointment)
        }
    }

    override fun getItemCount() = appointments.size
}