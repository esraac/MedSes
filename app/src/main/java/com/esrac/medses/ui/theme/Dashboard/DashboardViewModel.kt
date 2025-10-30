package com.esrac.medses.ui.theme.Dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    var userName by mutableStateOf("Esra")
        private set

    var appointments by mutableStateOf(
        listOf(
            "10/11/2025 - Kardiyoloji",
            "15/11/2025 - Dahiliye",
            "20/11/2025 - Göz"
        )
    )
        private set

    fun startVoiceInput() {
        // Sesli semptom girişini başlat
        println("Sesli semptom girişine geçiliyor...")
    }
}

