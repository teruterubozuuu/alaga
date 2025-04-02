package com.example.alaga.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.alaga.R
import com.example.alaga.models.Appointment

class AppointmentAdapter(private val appointmentList: List<Appointment>, private val onUpdateStatus: (Appointment) -> Unit) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> () {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPatient: TextView = itemView.findViewById(R.id.txtPatient)
        val txtDoctor: TextView = itemView.findViewById(R.id.txtDoctor)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val btnUpdate: Button = itemView.findViewById(R.id.btnUpdate)
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentAdapter.AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentAdapter.AppointmentViewHolder, position: Int) {
        val appointment = appointmentList[position]
        holder.txtPatient.text = "Patient ID: ${appointment.patientId}"
        holder.txtDoctor.text = "Doctor ID: ${appointment.doctorId}"
        holder.txtDate.text = "Date: ${appointment.appointmentDate}"
        holder.txtStatus.text = "Status: ${appointment.status}"

        holder.btnUpdate.setOnClickListener {
            onUpdateStatus(appointment)
        }
    }

    override fun getItemCount(): Int = appointmentList.size


}
