package com.example

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.command.JarvisCommandEngine
import com.example.model.ActionType
import com.example.model.MessageSender
import com.example.model.SystemTelemetry
import com.example.model.VoiceMessage
import com.example.speech.JarvisVoiceManager
import com.example.ui.theme.JarvisThemePalette
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

  private val commandEngine = JarvisCommandEngine(application)

  private val _messages = MutableStateFlow<List<VoiceMessage>>(
    listOf(
      VoiceMessage(
        text = "Jarvis Mark VII online. Awaiting your voice command, Boss. Say \"Jarvis\" or tap the Arc Reactor.",
        sender = MessageSender.JARVIS,
        actionType = ActionType.GREETING
      )
    )
  )
  val messages: StateFlow<List<VoiceMessage>> = _messages.asStateFlow()

  private val _themePalette = MutableStateFlow(JarvisThemePalette.ARC_CYAN)
  val themePalette: StateFlow<JarvisThemePalette> = _themePalette.asStateFlow()

  private val _batteryLevel = MutableStateFlow(100)
  private val _isCharging = MutableStateFlow(false)
  private val _isUrduMode = MutableStateFlow(true)
  private val _continuousMode = MutableStateFlow(false)

  // One-time navigation / external intents to launch
  private val _externalIntentEvents = MutableSharedFlow<Intent>()
  val externalIntentEvents: SharedFlow<Intent> = _externalIntentEvents.asSharedFlow()

  val voiceManager = JarvisVoiceManager(application) { recognizedText ->
    handleUserVoiceInput(recognizedText)
  }

  // Combined Telemetry StateFlow
  val telemetry: StateFlow<SystemTelemetry> = combine(
    combine(_batteryLevel, _isCharging, _isUrduMode) { b, c, u -> Triple(b, c, u) },
    combine(voiceManager.isListening, voiceManager.isSpeaking, voiceManager.rmsDb) { l, s, r -> Triple(l, s, r) }
  ) { (battery, charging, isUrdu), (listening, speaking, rms) ->
    SystemTelemetry(
      batteryLevel = battery,
      isCharging = charging,
      coreStatus = "100%",
      isListening = listening,
      isSpeechActive = speaking,
      languageMode = if (isUrdu) "URDU / EN" else "EN-US",
      rmsDb = rms
    )
  }.let { combinedFlow ->
    val initial = SystemTelemetry()
    val state = MutableStateFlow(initial)
    viewModelScope.launch {
      combinedFlow.collect { state.value = it }
    }
    state.asStateFlow()
  }

  private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level >= 0 && scale > 0) {
          _batteryLevel.value = ((level.toFloat() / scale.toFloat()) * 100).toInt()
        }
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        _isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING ||
          status == BatteryManager.BATTERY_STATUS_FULL
      }
    }
  }

  init {
    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    application.registerReceiver(batteryReceiver, filter)
  }

  fun toggleListening() {
    if (voiceManager.isListening.value) {
      voiceManager.stopListening()
    } else {
      voiceManager.startListening()
    }
  }

  fun toggleContinuousMode() {
    val nextMode = !_continuousMode.value
    _continuousMode.value = nextMode
    voiceManager.toggleContinuousMode(nextMode)
    val modeText = if (nextMode) "Continuous always-listening mode engaged." else "Tap mode activated."
    addJarvisResponse(modeText, ActionType.UNKNOWN)
    voiceManager.speak(modeText)
  }

  fun toggleLanguage() {
    val nextUrdu = !_isUrduMode.value
    _isUrduMode.value = nextUrdu
    voiceManager.setLanguage(nextUrdu)
    val langText = if (nextUrdu) "Voice language set to Urdu & Roman Urdu." else "Voice language set to English (US)."
    addJarvisResponse(langText, ActionType.UNKNOWN)
    voiceManager.speak(langText)
  }

  fun cycleTheme() {
    val palettes = JarvisThemePalette.entries
    val nextIdx = (palettes.indexOf(_themePalette.value) + 1) % palettes.size
    _themePalette.value = palettes[nextIdx]
  }

  fun handleUserVoiceInput(command: String) {
    // 1. Record user message
    _messages.update { current ->
      current + VoiceMessage(
        text = command,
        sender = MessageSender.USER
      )
    }

    // 2. Process command
    val result = commandEngine.processCommand(
      rawText = command,
      batteryLevel = _batteryLevel.value,
      isCharging = _isCharging.value
    )

    // 3. Handle theme change if triggered
    if (result.triggerThemeChange) {
      cycleTheme()
    }

    // 4. Add Jarvis reply to transmission log
    addJarvisResponse(result.replyText, result.actionType)

    // 5. Speak response
    voiceManager.speak(result.replyText)

    // 6. Launch external intent if requested
    result.externalIntent?.let { intent ->
      viewModelScope.launch {
        _externalIntentEvents.emit(intent)
      }
    }
  }

  private fun addJarvisResponse(text: String, actionType: ActionType) {
    _messages.update { current ->
      current + VoiceMessage(
        text = text,
        sender = MessageSender.JARVIS,
        actionType = actionType
      )
    }
  }

  fun replaySpeech(text: String) {
    voiceManager.speak(text)
  }

  fun clearHistory() {
    _messages.value = emptyList()
  }

  override fun onCleared() {
    super.onCleared()
    try {
      getApplication<Application>().unregisterReceiver(batteryReceiver)
    } catch (_: Exception) {}
    voiceManager.destroy()
  }
}
