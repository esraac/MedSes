package com.esrac.medses.ui.theme.Register

import android.Manifest
import android.R
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.esrac.medses.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController,
                   viewModel: RegisterViewModel = viewModel()
) {

    val ad by viewModel::ad
    val soyAd by viewModel::soyAd
    val tcKimlik by viewModel::tcKimlik
    val cinsiyet by viewModel::cinsiyet
    val dogumTarihi by viewModel::dogumTarihi
    val isListening by viewModel::isListening
    val registrationState by viewModel::registrationState
    val pin by viewModel::pin

    val context = LocalContext.current
    val tts = remember { TextToSpeech(context) {} }

    // Form geçerlilik kontrolü
    val isFormValid = ad.isNotBlank() && soyAd.isNotBlank() &&
            tcKimlik.length == 11 && dogumTarihi.isNotBlank() && cinsiyet.isNotBlank() && pin.length == 4

    // Mikrofon izin isteği için launcher
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.startVoiceRegistration()
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

    // Doğum tarihi picker için state
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Üye Ol", style = MaterialTheme.typography.headlineMedium )},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DefaultTextColor,

                )
            )
        },
        containerColor = Color.White // Arka plan rengi
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // ------------------------------------
            // 1. UYARI
            // ------------------------------------

            Text(
                text = "Tüm alanların doldurulması zorunludur.",
                color = BackgroundLightGray,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            )

            // ------------------------------------
            // 2. FORM ALANLARI
            // ------------------------------------

            // Ad
            FormLabelField(label = "Ad", isRequired = true)
            OutlinedTextField(
                value = ad,
                onValueChange = { viewModel.ad = it },
                placeholder = { Text("Ad") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isListening,
                singleLine = true,
                colors = CustomTextFieldColors(isListening)
            )

            // Soyad
            FormLabelField(label = "Soyad", isRequired = true)
            OutlinedTextField(
                value = soyAd,
                onValueChange = { viewModel.soyAd = it },
                placeholder = { Text("Soyad") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isListening,
                singleLine = true,
                colors = CustomTextFieldColors(isListening)
            )

            // T.C. Kimlik No
            FormLabelField(label = "T.C. Kimlik No", isRequired = true)
            OutlinedTextField(
                value = tcKimlik,
                onValueChange = { viewModel.tcKimlik = it },
                placeholder = { Text("T.C. Kimlik No") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = isListening,
                singleLine = true,
                visualTransformation = if (tcKimlik.length == 11 && !isListening) NumberMaskVisualTransformation("***********") else VisualTransformation.None,
                colors = CustomTextFieldColors(isListening)
            )

            // Doğum Tarihi
            FormLabelField(label = "Doğum Tarihi", isRequired = true)
            DatePickerField(
                date = dogumTarihi,
                onClick = { showDatePicker = true },
                readOnly = isListening
            )

            // Cinsiyet Dropdown
            FormLabelField(label = "Cinsiyet", isRequired = true)
            GenderDropdownField(
                selectedGender = cinsiyet,
                readOnly = isListening,
                onGenderSelected = { viewModel.cinsiyet = it } // Manuel giriş açılabilir
            )

            FormLabelField(label = "PIN", isRequired = true)
            OutlinedTextField(
                value = pin,
                onValueChange = {
                    if(it.length == 4) viewModel.pin = it
                },
                placeholder = { Text("Dört Haneli PIN")},
                modifier = Modifier.fillMaxWidth(),
                readOnly = isListening,
                singleLine = true,
                colors = CustomTextFieldColors(isListening)
            )
            // ------------------------------------
            // 3. İLERİ BUTONU
            // ------------------------------------
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.registerUser() },
                enabled = isFormValid && !isListening,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedSesGreen,
                    disabledContainerColor = MedSesGreen.copy(alpha = 0.5f),
                    contentColor = MedSesWhite
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("İleri", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "İleri"
                )
            }

            // ------------------------------------
            // 4. SESLİ BAŞLATMA ve DURUM
            // ------------------------------------
            Spacer(modifier = Modifier.height(20.dp))

            // Durum/Hata Mesajı
            if (registrationState.isNotBlank()) {
                RegistrationStateMessage(registrationState)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mikrofonla Başlat Butonu
            Button(
                onClick = { requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                enabled = !isListening,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MedSesBlue,
                    contentColor = MedSesWhite,
                    disabledContainerColor = MedSesBlue.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Sesli Kaydı Başlat",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isListening) "Asistan Dinliyor..." else "Mikrofonla Kayıt Başlat", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            // Giriş Ekranına Dön butonu
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Giriş Ekranına Dön", color = MedSesBlue)
            }
        }
    }

    // Doğum Tarihi Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val date = Date(selectedDateMillis)
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.dogumTarihi = formatter.format(date)
                    }
                }) {
                    Text("Tamam", color = MedSesBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("İptal", color = MedSesBlue)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ----------------------------------------------------
// --- YARDIMCI COMPOSABLE VE RENK FONKSİYONLARI ---
// ----------------------------------------------------

@Composable
fun FormLabelField(label: String, isRequired: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        if (isRequired) {
            Text(
                text = "*",
                color = ErrorRed,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        Text(
            text = "$label:",
            fontWeight = FontWeight.Normal,
            color = DefaultTextColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdownField(
    selectedGender: String,
    readOnly: Boolean,
    onGenderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val genders = listOf("Erkek", "Kadın")

    ExposedDropdownMenuBox(
        expanded = expanded && !readOnly,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedGender.ifBlank { "Cinsiyet" },
            onValueChange = { /* Sadece görsel */ },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text("Cinsiyet", color = Color.Gray.copy(alpha = 0.7f)) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            colors = CustomTextFieldColors(readOnly)
        )

        if (!readOnly) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genders.forEach { gender ->
                    DropdownMenuItem(
                        text = { Text(gender) },
                        onClick = {
                            onGenderSelected(gender) // ViewModel'i güncelle
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerField(
    date: String,
    onClick: () -> Unit,
    readOnly: Boolean
) {
    OutlinedTextField(
        value = if (date.isNotBlank()) date else "",
        onValueChange = { /* Sadece görsel */ },
        readOnly = true,
        placeholder = { Text("Doğum Tarihi", color = Color.Gray.copy(alpha = 0.7f)) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Takvim",
                tint = if (readOnly) Color.Gray.copy(alpha = 0.6f) else MedSesBlue,
                modifier = Modifier.clickable(indication = null,
                    interactionSource = remember { MutableInteractionSource() }) { onClick() }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CustomTextFieldColors(readOnly)
    )
}

@Composable
fun RegistrationStateMessage(state: String) {
    val isSuccess = state.contains("başarılı")
    val bgColor = if (isSuccess) MedSesGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)
    val contentColor = if (isSuccess) MedSesGreen else ErrorRed

    Text(
        text = state,
        color = contentColor,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextFieldColors(readOnly: Boolean): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MedSesBlue,
        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f),
        disabledBorderColor = Color.Gray.copy(alpha = 0.2f),
        cursorColor = MedSesBlue,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.LightGray.copy(alpha = 0.1f),
        focusedPlaceholderColor = Color.Gray.copy(alpha = 0.7f),
        unfocusedPlaceholderColor = Color.Gray.copy(alpha = 0.7f),
        // Sadece okuma modunda (isListening=true) metin rengini biraz solgun yapıyoruz
        disabledTextColor = DefaultTextColor.copy(alpha = 0.6f)
    )
}

// TC Kimlik maskesi için VisualTransformation
class NumberMaskVisualTransformation(private val mask: String) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val trimmed = if (text.text.length >= mask.length) mask else text.text.padEnd(mask.length, '*')
        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(trimmed),
            object : androidx.compose.ui.text.input.OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = offset
                override fun transformedToOriginal(offset: Int): Int = offset
            }
        )
    }
}