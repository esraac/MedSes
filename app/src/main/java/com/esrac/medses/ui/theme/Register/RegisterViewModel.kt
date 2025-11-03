package com.esrac.medses.ui.theme.Register

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.privacysandbox.ads.adservices.adid.AdId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.util.Locale

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    var tcKimlik by mutableStateOf("")
    var ad by mutableStateOf("")
    var soyAd by mutableStateOf("")
    var cinsiyet by mutableStateOf("")          // Kadın veya Erkek
    var dogumTarihi by mutableStateOf("")       // Yıl/Ay/Gün formatında tutulacak
    var registrationState by mutableStateOf("")
    var isListening by mutableStateOf(false)

    var pin by mutableStateOf("")

    private val context = application.applicationContext
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        setupTTS()
        setupSpeechRecognizer()
    }

    private enum class Stage { AD, SOYAD, TCKIMLIK, DOGUM_TARIHI, CINSIYET, PIN, NONE }
    private var listeningState = Stage.NONE

    fun startVoiceRegistration(){       // Kullanıcın kaydı başlattığı metot
        listeningState = Stage.AD
        speakAndListen("Üyelik için lütfen sadece adınızı söyleyin.")
    }

    private fun continueFlow(){     // Her aşama başarıyla tamamlandığında çağrılır.
        when (listeningState){
            Stage.AD -> {
                listeningState = Stage.SOYAD
                speakAndListen("Şimdi lütfen sadece soyadınızı söyleyin.")
            }
            Stage.SOYAD -> {
                listeningState = Stage.TCKIMLIK
                speakAndListen("Lütfen 11 haneli TC kimlik numaranızı söyleyin.")
            }
            Stage.TCKIMLIK -> {
                listeningState = Stage.DOGUM_TARIHI
                speakAndListen("Lütfen doğum tarihinizi gün, ay ve yıl olarak rakamlarla söyleyin. Örneğin 15 01 2002 şeklinde söyleyebilirsiniz.")
            }
            Stage.DOGUM_TARIHI -> {
                listeningState = Stage.CINSIYET
                speakAndListen("Lütfen cinsiyetinizi kadın ya da erkek olarak belirtin.")
            }
            Stage.CINSIYET -> {
                listeningState = Stage.PIN
                speakAndListen("Lütfen 4 haneli bir şifre oluşturun.")
            }
            Stage.PIN -> {
                listeningState = Stage.NONE
                registerUser()
            }
            Stage.NONE -> {}
        }
    }

    private fun setupTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("tr", "TR")
            }
        }
    }

    private fun setupSpeechRecognizer(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener{
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if(!matches.isNullOrEmpty()){
                    val spokenText = matches[0]

                    when(listeningState){
                        Stage.AD -> ad = spokenText.capitalizeWords()
                        Stage.SOYAD -> soyAd = spokenText.capitalizeWords()
                        Stage.TCKIMLIK -> tcKimlik = normalizeSpokenNumbers(spokenText)
                        Stage.DOGUM_TARIHI -> dogumTarihi = formatSpokenDate(spokenText)
                        Stage.CINSIYET -> cinsiyet = spokenText.normalizeGender()
                        Stage.PIN -> pin = normalizeSpokenNumbers(spokenText)
                        Stage.NONE -> {}
                    }
                }
                isListening = false
                continueFlow()
            }

            override fun onError(error: Int) {
                isListening = false
                speak("Sizi anlayamadım, lütfen tekrar deneyin.")
                startListeningForCurrentStage()
            }
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
    // Ad/Soyad'ın baş harflerini büyük yapmak için kullanılır.
    private fun String.capitalizeWords(): String = lowercase().split(" ").joinToString(" "){
        it.replaceFirstChar { char -> if( char.isLowerCase()) char.titlecase(Locale("tr","TR")) else char.toString() }
    }

    // Söylenen rakam kelimelerini (örneğin "bir iki") sayı dizisine çevirir. (TC Kimlik için ideal)
    private fun normalizeSpokenNumbers(spoken: String): String {
        val map = mapOf(
            "sıfır" to "0", "0" to "0",
            "bir" to "1", "1" to "1",
            "iki" to "2", "2" to "2",
            "üç" to "3", "3" to "3",
            "dört" to "4", "4" to "4",
            "beş" to "5", "5" to "5",
            "altı" to "6", "6" to "6",
            "yedi" to "7", "7" to "7",
            "sekiz" to "8", "8" to "8",
            "dokuz" to "9", "9" to "9"
        )
        return spoken.lowercase()
            .split(" ", ",", "-", ".")
            .mapNotNull { map[it] }
            .joinToString("")
    }

    // Söylenen tarihi (GG AA YYYY) YYYY-MM-DD formatına dönüştürür.
    private fun formatSpokenDate(spoken: String): String {
        // Rakam ve boşlukları al, birden fazla boşluğu tek boşluğa indirge.
        val numbers = spoken.filter { it.isDigit() || it.isWhitespace() }
            .split("\\s+".toRegex()).filter { it.isNotBlank() }

        if (numbers.size == 3) {
            val day = numbers[0].padStart(2, '0')
            val month = numbers[1].padStart(2, '0')
            val year = numbers[2]
            return "$year-$month-$day"
        }
        return "" // Geçersiz format
    }

    // Söylenen cinsiyeti standartlaştırır.
    private fun String.normalizeGender(): String {
        val lowerCaseText = this.lowercase(Locale("tr", "TR"))
        return when {
            lowerCaseText.contains("erkek") -> "Erkek"
            lowerCaseText.contains("kadın") || lowerCaseText.contains("kadin") -> "Kadın"
            else -> ""
        }
    }
    private fun speakAndListen(message: String){        // TTs ve STT Senkronizasyonu
        audioManager.setMicrophoneMute(true)            // Gürültüyü engellemek için mikrofonu sessize alır.
        val utteranceId = "voice_registration"
        tts?.speak(message,TextToSpeech.QUEUE_FLUSH, null,utteranceId)

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?){}
            override fun onDone(utteranceId: String?){
                if(utteranceId == "voice_registration"){
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        audioManager.setMicrophoneMute(false)
                        startListeningForCurrentStage()
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                audioManager.setMicrophoneMute(false)
            }
        })
    }

    private fun startListeningForCurrentStage(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,"tr-TR")
        }
        isListening = true
        speechRecognizer?.startListening(intent)
    }

    fun registerUser() {
        val isValid = ad.isNotBlank() &&
                soyAd.isNotBlank() &&
                tcKimlik.length == 11 && tcKimlik.all { it.isDigit() } &&
                dogumTarihi.matches("\\d{4}-\\d{2}-\\d{2}".toRegex()) &&
                (cinsiyet == "Kadın" || cinsiyet == "Erkek") &&
                pin.length == 4 && pin.all { it.isDigit() }
        if(isValid){
            registrationState = "Üyelik başarıyla gerçekleştirildi. Hoş geldiniz $ad $soyAd."
            speak("Üyelik bilgileriniz başarıyla kaydedildi. Hoş geldiniz $ad.")
            println("Kayıt yapıldı")
        }else{
            registrationState = "Üyelik bilgileri eksik veya hatalı. Lütfen tüm adımları tamamlayıp tekrar deneyin."
            speak("Üyelik bilgileri eksik veya hatalı.")
        }
        // TODO: Bu kısımda kullanıcı bütün adımları tek tek uğraşmak zorunda kalmasa sadece hatalı kısmı tekrar denese nasıl olur bak
    }

    private fun speak(text: String) {
        tts?.language = Locale("tr", "TR")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH,null,null)
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }

}