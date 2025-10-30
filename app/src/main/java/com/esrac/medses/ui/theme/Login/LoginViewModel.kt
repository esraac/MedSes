package com.esrac.medses.ui.theme.Login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var loginState by mutableStateOf(false)
        private set

    fun login(tcNo: String, password: String) {
        loginState = (tcNo == "12345678901" && password == "1234")
    }
}