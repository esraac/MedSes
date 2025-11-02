package com.esrac.medses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esrac.medses.ui.theme.Dashboard.DashboardScreen
import com.esrac.medses.ui.theme.Dashboard.DashboardViewModel
import com.esrac.medses.ui.theme.Login.LoginScreen
import com.esrac.medses.ui.theme.Login.LoginViewModel
import com.esrac.medses.ui.theme.MedSesTheme
import com.esrac.medses.ui.theme.Register.RegisterScreen
import com.esrac.medses.ui.theme.Register.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedSesTheme {
                MedSesApp()
            }
        }
    }
}

@Composable
fun MedSesApp() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("login") {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    navController = navController,
                    viewModel = loginViewModel
                )
            }

            composable("dashboard") {
                val dashboardViewModel: DashboardViewModel = viewModel()
                DashboardScreen(viewModel = dashboardViewModel)
            }
            composable("register"){
                val registerViewModel: RegisterViewModel = viewModel()
                RegisterScreen(
                    navController = navController,
                    viewModel = registerViewModel
                )
            }
        }
    }
}