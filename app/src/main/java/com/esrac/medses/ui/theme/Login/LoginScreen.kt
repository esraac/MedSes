package com.esrac.medses.ui.theme.Login

import android.Manifest
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.util.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    var tcKimlikState = viewModel.tcKimlik
    var pinState = viewModel.pin
    var loginState = viewModel.loginState
    var isListening = viewModel.isListening

    val context = LocalContext.current
    val tts = remember { TextToSpeech(context) {} }

    LaunchedEffect(loginState) {
        if (loginState == "Giriş başarılı") {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.startVoiceLogin()
            } else {
                tts.language = Locale("tr", "TR")
                tts.speak(
                    "Mikrofon izni verilmedi.",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("MedSes - Sesli Giriş", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = tcKimlikState,
                onValueChange = { tcKimlikState= it },
                label = { Text("TC Kimlik") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pinState,
                onValueChange = { pinState = it },
                label = { Text("4 haneli PIN") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.signIn() }, modifier = Modifier.fillMaxWidth()) {
                Text("Giriş Yap")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("🎤 Mikrofonla Giriş Yap")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(loginState)

            if (isListening) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Dinleniyor...")
            }
        }
    }
}
