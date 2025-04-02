package com.example.alaga.models

data class Appointment(
    val id: Int,
    val patientId: Int,
    val doctorId: Int,
    val appointmentDate: String,
    var status: String,
    val notes: String
)
