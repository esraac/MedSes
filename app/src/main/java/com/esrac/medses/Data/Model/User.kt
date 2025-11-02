package com.esrac.medses.Data.Model

data class User(
    val id: Long? = null,
    val name: String,
    val tcKimlik: String,
    val password: String,
    val phone: String? = null
)

