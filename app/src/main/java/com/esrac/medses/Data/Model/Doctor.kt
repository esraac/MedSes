package com.esrac.medses.Data.Model

data class Doctor(
    val id: Long? = null,
    val name: String,
    val specialization: String,
    val clinic: Clinic
)

