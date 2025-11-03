package com.esrac.medses.ui.theme.Login

import android.Manifest
import com.esrac.medses.R
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.esrac.medses.ui.theme.DefaultTextColor
import com.esrac.medses.ui.theme.MedSesBlue
import com.esrac.medses.ui.theme.MedSesOrange
import com.esrac.medses.ui.theme.MedSesWhite
import java.util.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val tcKimlikState = viewModel.tcKimlik
    val pinState = viewModel.pin
    val loginState = viewModel.loginState
    val isListening = viewModel.isListening

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.medses),
                contentDescription = "MedSes Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                "MedSes Giriş",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = DefaultTextColor,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Medikal Sese Hoş Geldiniz!",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = DefaultTextColor
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = tcKimlikState,
                onValueChange = { viewModel.tcKimlik = it },
                label = { Text("TC Kimlik") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pinState,
                onValueChange = { viewModel.pin = it },
                label = { Text("4 haneli PIN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,

                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.signIn() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedSesBlue,
                    contentColor = MedSesWhite
                )
            ) {
                Text("Giriş Yap", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedSesOrange,
                    contentColor = MedSesWhite
                )
            ) {
                Text("🎤 Mikrofonla Giriş Yap", style = MaterialTheme.typography.titleMedium)
            }

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text(
                    "Hesabınız yok mu? Kayıt Ol",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (loginState.isNotBlank()) {
                Text(
                    loginState,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isListening) {
                Text(
                    "Dinleniyor...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}