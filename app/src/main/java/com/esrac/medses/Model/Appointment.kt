package com.esrac.medses.Model

data class Appointment(
    val id: String,
    val userId: String,
    val clinicId: String,
    val date: String,
    val time: String,
    val status: String
)
