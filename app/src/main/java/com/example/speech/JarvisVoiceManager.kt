package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class JarvisVoiceManager(
  private val context: Context,
  private val onCommandRecognized: (String) -> Unit
) : RecognitionListener, TextToSpeech.OnInitListener {

  private val tag = "JarvisVoiceManager"
  private val mainHandler = Handler(Looper.getMainLooper())

  private var speechRecognizer: SpeechRecognizer? = null
  private var textToSpeech: TextToSpeech? = null
  private var isTtsReady = false

  private val _isListening = MutableStateFlow(false)
  val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _rmsDb = MutableStateFlow(0f)
  val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

  var continuousMode: Boolean = false
  private var shouldRestart = false
  private var selectedLocale: Locale = Locale("ur", "PK") // Urdu / Pakistan with fallback

  init {
    initSpeechRecognizer()
    initTts()
  }

  private fun initSpeechRecognizer() {
    mainHandler.post {
      try {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
          speechRecognizer?.destroy()
          speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(this@JarvisVoiceManager)
          }
        } else {
          Log.w(tag, "SpeechRecognizer is not available on this device")
        }
      } catch (e: Exception) {
        Log.e(tag, "Failed to initialize SpeechRecognizer", e)
      }
    }
  }

  private fun initTts() {
    textToSpeech = TextToSpeech(context, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isTtsReady = true
      textToSpeech?.let { tts ->
        tts.setPitch(0.95f)
        tts.setSpeechRate(1.02f)
        // Check for Urdu language or fallback to UK/US English
        val urduLocale = Locale("ur", "PK")
        val result = tts.setLanguage(urduLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          tts.language = Locale.US
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
            _isSpeaking.value = true
          }

          override fun onDone(utteranceId: String?) {
            _isSpeaking.value = false
            if (continuousMode && shouldRestart) {
              mainHandler.postDelayed({ startListening() }, 400)
            }
          }

          @Deprecated("Deprecated in Java")
          override fun onError(utteranceId: String?) {
            _isSpeaking.value = false
          }
        })
      }
    } else {
      Log.e(tag, "TTS Initialization failed")
    }
  }

  fun toggleContinuousMode(enable: Boolean) {
    continuousMode = enable
    if (enable) {
      shouldRestart = true
      startListening()
    } else {
      shouldRestart = false
      stopListening()
    }
  }

  fun setLanguage(isUrdu: Boolean) {
    selectedLocale = if (isUrdu) Locale("ur", "PK") else Locale.US
    if (isListening.value) {
      stopListening()
      mainHandler.postDelayed({ startListening() }, 300)
    }
  }

  fun startListening() {
    if (_isSpeaking.value) return // Don't listen while Jarvis is speaking
    mainHandler.post {
      try {
        if (speechRecognizer == null) {
          initSpeechRecognizer()
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
          putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLocale.toLanguageTag())
          putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLocale.toLanguageTag())
          putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
          putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
          putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        speechRecognizer?.startListening(intent)
        _isListening.value = true
      } catch (e: Exception) {
        Log.e(tag, "Error starting speech recognition", e)
        _isListening.value = false
      }
    }
  }

  fun stopListening() {
    mainHandler.post {
      try {
        speechRecognizer?.stopListening()
      } catch (e: Exception) {
        Log.e(tag, "Error stopping speech recognition", e)
      } finally {
        _isListening.value = false
        _rmsDb.value = 0f
      }
    }
  }

  fun speak(text: String, onDone: (() -> Unit)? = null) {
    if (!isTtsReady) {
      onDone?.invoke()
      return
    }
    mainHandler.post {
      // Pause listening while speaking to prevent echo feedback loop
      if (_isListening.value) {
        speechRecognizer?.stopListening()
        _isListening.value = false
      }
      val params = Bundle().apply {
        putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "JARVIS_${System.currentTimeMillis()}")
      }
      textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, params.getString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))
    }
  }

  fun stopSpeaking() {
    textToSpeech?.stop()
    _isSpeaking.value = false
  }

  // SpeechRecognizer callbacks
  override fun onReadyForSpeech(params: Bundle?) {
    _isListening.value = true
  }

  override fun onBeginningOfSpeech() {
    _isListening.value = true
  }

  override fun onRmsChanged(rmsdB: Float) {
    // Normalize and pass RMS level (usually -2dB to 10dB)
    _rmsDb.value = rmsdB.coerceIn(0f, 12f)
  }

  override fun onBufferReceived(buffer: ByteArray?) {}

  override fun onEndOfSpeech() {
    _isListening.value = false
    _rmsDb.value = 0f
  }

  override fun onError(error: Int) {
    _isListening.value = false
    _rmsDb.value = 0f
    Log.d(tag, "SpeechRecognizer error: $error")

    // Automatic restart if in continuous listening mode
    if (continuousMode && shouldRestart && !_isSpeaking.value) {
      mainHandler.postDelayed({
        startListening()
      }, 700)
    }
  }

  override fun onResults(results: Bundle?) {
    _isListening.value = false
    _rmsDb.value = 0f
    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
    val recognizedText = matches?.firstOrNull()?.trim()
    if (!recognizedText.isNullOrBlank()) {
      onCommandRecognized(recognizedText)
    } else if (continuousMode && shouldRestart && !_isSpeaking.value) {
      mainHandler.postDelayed({ startListening() }, 500)
    }
  }

  override fun onPartialResults(partialResults: Bundle?) {
    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
    val partial = matches?.firstOrNull()
    if (!partial.isNullOrBlank()) {
      _rmsDb.value = 6f
    }
  }

  override fun onEvent(eventType: Int, params: Bundle?) {}

  fun destroy() {
    try {
      speechRecognizer?.destroy()
      textToSpeech?.stop()
      textToSpeech?.shutdown()
    } catch (e: Exception) {
      Log.e(tag, "Error during destroy", e)
    }
  }
}
