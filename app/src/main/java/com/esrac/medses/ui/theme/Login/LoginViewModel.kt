package com.esrac.medses.ui.theme.Login

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
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    var tcKimlik by mutableStateOf("")
    var pin by mutableStateOf("")
    var loginState by mutableStateOf("")
    var isListening by mutableStateOf(false)

    private val context = application.applicationContext
    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var audioManager: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        setupTTS()
        setupSpeechRecognizer()
    }

    private enum class Stage { TCKIMLIK, PIN, NONE }
    private var listeningStage = Stage.NONE

    fun startVoiceLogin() {
        listeningStage = Stage.TCKIMLIK
        speakAndListen("Lütfen TC kimlik numaranızı söyleyin")
    }

    private fun continueFlow() {
        when (listeningStage) {
            Stage.TCKIMLIK -> {
                listeningStage = Stage.PIN
                speakAndListen("Lütfen 4 haneli PIN’inizi söyleyin.")
            }
            Stage.PIN -> {
                listeningStage = Stage.NONE
                signIn()
            }
            else -> {}
        }
    }

    private fun setupTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("tr", "TR")
            }
        }
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    when (listeningStage) {
                        Stage.TCKIMLIK -> tcKimlik = matches[0].replace(" ", "").lowercase()
                        Stage.PIN -> pin = convertSpokenToPin(matches[0])
                        Stage.NONE -> TODO()
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

    private fun convertSpokenToPin(spoken: String): String {
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


    private fun speakAndListen(message: String) {
        audioManager.setMicrophoneMute(true)
        val utteranceId = "voice_login"
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                if (utteranceId == "voice_login") {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        audioManager.setMicrophoneMute(false)
                        startListeningForCurrentStage()
                    }
                }
            }
            override fun onError(utteranceId: String?) { audioManager.setMicrophoneMute(false) }
        })
    }

    private fun startListeningForCurrentStage() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
        }
        isListening = true
        speechRecognizer?.startListening(intent)
    }

    fun signIn() {
        if (tcKimlik == "12345678912" && pin == "1234") {
            loginState = "Giriş başarılı"
        } else {
            loginState = "E-posta veya PIN hatalı"
        }
    }

    private fun speak(text: String) {
        tts?.language = Locale("tr", "TR")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
