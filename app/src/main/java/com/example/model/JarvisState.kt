package com.example.model

enum class MessageSender {
  USER,
  JARVIS
}

enum class ActionType {
  GREETING,
  OPEN_APP,
  TIME,
  DATE,
  BATTERY,
  SEARCH,
  THEME_CHANGE,
  DIAGNOSTIC,
  HELP,
  UNKNOWN
}

data class VoiceMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val text: String,
  val sender: MessageSender,
  val timestamp: Long = System.currentTimeMillis(),
  val actionType: ActionType = ActionType.UNKNOWN
)

data class SystemTelemetry(
  val batteryLevel: Int = 100,
  val isCharging: Boolean = false,
  val coreStatus: String = "100%",
  val isListening: Boolean = false,
  val isSpeechActive: Boolean = false,
  val languageMode: String = "Urdu / English",
  val rmsDb: Float = 0f
)
