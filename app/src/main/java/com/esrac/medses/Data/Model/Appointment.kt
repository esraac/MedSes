package com.esrac.medses.Data.Model

data class Appointment(
    val id: Long? = null,
    val user: User,
    val doctor: Doctor,
    val appointmentDateTime: String,
    val status: String,
    val notes: String? = null
)

