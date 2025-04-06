package com.example.alaga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alaga.R
import com.example.alaga.models.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class PatientAppointmentAdapter(
    private val context: Context,
    private val appointments: List<Appointment>,
    private val userRole: String,
    private val onCancel: (Appointment) -> Unit,
    private val onReschedule: (Appointment) -> Unit,
    private val onAccept: (Appointment) -> Unit // New callback for accept action
) : RecyclerView.Adapter<PatientAppointmentAdapter.AppointmentViewHolder>() {

    private val dbHelper: DatabaseHelper by lazy { DatabaseHelper(context) }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val personName: TextView = itemView.findViewById(R.id.doctorNameTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
        val cancelBtn: Button = itemView.findViewById(R.id.cancelButton)
        val rescheduleBtn: Button = itemView.findViewById(R.id.rescheduleButton)
        val acceptBtn: Button = itemView.findViewById(R.id.acceptButton) // New accept button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment_patient, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        when (userRole) {
            "Doctor" -> {
                val patientName = dbHelper.getUserNameById(appointment.patientId)
                holder.personName.text = "Patient: $patientName"
            }
            "Patient" -> {
                val doctorName = dbHelper.getUserNameById(appointment.doctorId)
                holder.personName.text = "Doctor: $doctorName"
            }
            else -> {
                val patientName = dbHelper.getUserNameById(appointment.patientId)
                val doctorName = dbHelper.getUserNameById(appointment.doctorId)
                holder.personName.text = "Dr. $doctorName → $patientName"
            }
        }

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


        if (appointment.status == "Pending") {
            when (userRole) {
                "Patient" -> {
                    holder.cancelBtn.visibility = View.VISIBLE
                    holder.rescheduleBtn.visibility = View.VISIBLE
                    holder.acceptBtn.visibility = View.GONE
                }
                "Doctor" -> {
                    holder.acceptBtn.visibility = View.VISIBLE
                    holder.cancelBtn.visibility = View.VISIBLE
                    holder.rescheduleBtn.visibility = View.VISIBLE
                }
                "Nurse" -> {
                    holder.acceptBtn.visibility = View.VISIBLE
                    holder.cancelBtn.visibility = View.VISIBLE
                    holder.rescheduleBtn.visibility = View.VISIBLE
                }
                else -> {
                    holder.cancelBtn.visibility = View.GONE
                    holder.rescheduleBtn.visibility = View.GONE
                    holder.acceptBtn.visibility = View.GONE
                }
            }
        } else {
            holder.cancelBtn.visibility = View.GONE
            holder.rescheduleBtn.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
        }

        holder.cancelBtn.setOnClickListener {
            onCancel(appointment)
        }

        holder.rescheduleBtn.setOnClickListener {
            onReschedule(appointment)
        }

        holder.acceptBtn.setOnClickListener {
            onAccept(appointment)
        }
    }

    override fun getItemCount() = appointments.size
}