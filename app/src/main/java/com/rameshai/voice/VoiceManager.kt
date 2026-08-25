package com.rameshai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.rameshai.model.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/**
 * Wraps Android's built-in SpeechRecognizer (STT) and TextToSpeech (TTS).
 * These are on-device system APIs — they work without any extra key, but
 * quality/language coverage depends on what the phone's Google app
 * supports (hi-IN is broadly supported on modern Android).
 */
class VoiceManager(private val context: Context) {

    private val _state = MutableStateFlow(VoiceState.IDLE)
    val state: StateFlow<VoiceState> = _state

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText

    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    fun init(onTtsReady: () -> Unit = {}) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                onTtsReady()
            }
        }
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { _state.value = VoiceState.SPEAKING }
            override fun onDone(utteranceId: String?) { _state.value = VoiceState.IDLE }
            override fun onError(utteranceId: String?) { _state.value = VoiceState.IDLE }
        })
    }

    fun setLanguage(locale: Locale) {
        tts?.language = locale
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    /** Starts listening. [languageTag] e.g. "hi-IN" or "en-IN". */
    fun startListening(
        languageTag: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition isn't available on this device.")
            return
        }
        stopListening()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { _state.value = VoiceState.LISTENING }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { _state.value = VoiceState.THINKING }
                override fun onError(error: Int) {
                    _state.value = VoiceState.IDLE
                    onError("Voice recognition error (code $error)")
                }
                override fun onResults(results: Bundle?) {
                    val text = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull().orEmpty()
                    _partialText.value = ""
                    if (text.isNotBlank()) onResult(text)
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    _partialText.value = partialResults
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull().orEmpty()
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        recognizer?.startListening(intent)
    }

    fun stopListening() {
        recognizer?.stopListening()
        recognizer?.destroy()
        recognizer = null
        if (_state.value == VoiceState.LISTENING) _state.value = VoiceState.IDLE
    }

    /** Call when the user starts speaking again to interrupt AI speech. */
    fun interruptSpeaking() {
        tts?.stop()
        _state.value = VoiceState.IDLE
    }

    fun speak(text: String, utteranceId: String = "ramesh_tts") {
        if (!ttsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun pauseSpeaking() = tts?.stop()

    fun release() {
        stopListening()
        tts?.shutdown()
    }
}
