package com.esrac.medses.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esrac.medses.ui.theme.Dashboard.DashboardScreen
import com.esrac.medses.ui.theme.Login.LoginScreen

@Composable
fun MedSesNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardScreen()
        }
    }
}
