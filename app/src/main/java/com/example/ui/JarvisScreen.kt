package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.JarvisViewModel
import com.example.ui.components.ArcReactorCore
import com.example.ui.components.AudioWaveVisualizer
import com.example.ui.components.CommandsHelpSheet
import com.example.ui.components.PwaCodeExportSheet
import com.example.ui.components.QuickActionHud
import com.example.ui.components.TelemetryHudBar
import com.example.ui.components.TranscriptCard

@Composable
fun JarvisScreen(
  viewModel: JarvisViewModel,
  onRequestAudioPermission: () -> Unit,
  modifier: Modifier = Modifier
) {
  val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
  val messages by viewModel.messages.collectAsStateWithLifecycle()
  val themePalette by viewModel.themePalette.collectAsStateWithLifecycle()

  var showCommandsSheet by remember { mutableStateOf(false) }
  var showPwaSheet by remember { mutableStateOf(false) }
  var manualInputText by remember { mutableStateOf("") }
  val focusManager = LocalFocusManager.current
  val scrollState = rememberScrollState()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = Color(0xFF040913)
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color(0xFF040913),
              Color(0xFF081224),
              Color(0xFF040913)
            )
          )
        ),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
          .verticalScroll(scrollState)
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Top Futuristic Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { viewModel.cycleTheme() }
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(Color.Transparent, CircleShape)
                .border(2.dp, themePalette.primary, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .background(themePalette.primary, CircleShape)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "JARVIS AI",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "MARK VII // ${themePalette.label.uppercase()}",
                color = themePalette.primary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            // PWA / Web App export button
            IconButton(
              onClick = { showPwaSheet = true },
              modifier = Modifier.testTag("open_pwa_sheet_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Public,
                contentDescription = "PWA Web App",
                tint = themePalette.primary,
                modifier = Modifier.size(22.dp)
              )
            }

            // Commands help button
            IconButton(
              onClick = { showCommandsSheet = true },
              modifier = Modifier.testTag("open_commands_sheet_btn")
            ) {
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Voice Commands Help",
                tint = themePalette.primary,
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }

        // Live Telemetry HUD Bar
        TelemetryHudBar(
          telemetry = telemetry,
          continuousMode = viewModel.voiceManager.continuousMode,
          onToggleContinuous = {
            onRequestAudioPermission()
            viewModel.toggleContinuousMode()
          },
          onToggleLanguage = { viewModel.toggleLanguage() }
        )

        // Arc Reactor Center Mic Component
        ArcReactorCore(
          isListening = telemetry.isListening,
          isSpeaking = telemetry.isSpeechActive,
          rmsDb = telemetry.rmsDb,
          glowColor = themePalette.primary,
          onToggleListen = {
            onRequestAudioPermission()
            viewModel.toggleListening()
          }
        )

        // Live Audio Waveform Visualizer
        AudioWaveVisualizer(
          isListening = telemetry.isListening,
          rmsDb = telemetry.rmsDb,
          color = themePalette.primary
        )

        // Activate / Deactivate Primary Button
        Button(
          onClick = {
            onRequestAudioPermission()
            viewModel.toggleListening()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (telemetry.isListening) Color(0xFFFF1744) else themePalette.primary,
            contentColor = Color(0xFF030914)
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("activate_jarvis_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (telemetry.isListening) "DEACTIVATE JARVIS MIC" else "ACTIVATE JARVIS (VOICE RECOGNITION)",
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }

        // Manual Text Command Input Bar (Supports testing without mic)
        OutlinedTextField(
          value = manualInputText,
          onValueChange = { manualInputText = it },
          placeholder = {
            Text(
              "Or type command: e.g. \"Jarvis YouTube kholo\"",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
          },
          trailingIcon = {
            IconButton(
              onClick = {
                if (manualInputText.isNotBlank()) {
                  viewModel.handleUserVoiceInput(manualInputText.trim())
                  manualInputText = ""
                  focusManager.clearFocus()
                }
              },
              modifier = Modifier.testTag("send_manual_command_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Command",
                tint = themePalette.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = themePalette.primary,
            unfocusedBorderColor = themePalette.primary.copy(alpha = 0.3f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF071222),
            unfocusedContainerColor = Color(0xFF071222)
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(onSend = {
            if (manualInputText.isNotBlank()) {
              viewModel.handleUserVoiceInput(manualInputText.trim())
              manualInputText = ""
              focusManager.clearFocus()
            }
          }),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("manual_command_input")
        )

        // Transmission Dialogue Log
        TranscriptCard(
          messages = messages,
          onReplaySpeech = { text -> viewModel.replaySpeech(text) },
          onClearHistory = { viewModel.clearHistory() }
        )

        // Direct Protocol Shortcuts HUD
        QuickActionHud(
          onCommandTriggered = { cmd -> viewModel.handleUserVoiceInput(cmd) }
        )

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }

  // Commands Help Sheet Modal
  if (showCommandsSheet) {
    CommandsHelpSheet(
      onDismiss = { showCommandsSheet = false },
      onSelectCommand = { cmd -> viewModel.handleUserVoiceInput(cmd) }
    )
  }

  // PWA Preview & Code Export Sheet
  if (showPwaSheet) {
    PwaCodeExportSheet(
      onDismiss = { showPwaSheet = false }
    )
  }
}
