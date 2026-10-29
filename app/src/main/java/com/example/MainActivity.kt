package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.JarvisScreen
import com.example.ui.theme.JarvisTheme

class MainActivity : ComponentActivity() {

  private val viewModel: JarvisViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val themePalette by viewModel.themePalette.collectAsStateWithLifecycle()

      // Handle external intents launched by voice commands
      LaunchedEffect(Unit) {
        viewModel.externalIntentEvents.collect { intent ->
          try {
            startActivity(intent)
          } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Could not open target application: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
          }
        }
      }

      // Audio permission launcher
      val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
      ) { isGranted ->
        if (isGranted) {
          viewModel.voiceManager.startListening()
        } else {
          Toast.makeText(
            this@MainActivity,
            "Microphone permission is required for Jarvis voice recognition.",
            Toast.LENGTH_LONG
          ).show()
        }
      }

      fun checkAndRequestAudioPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
          this@MainActivity,
          Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
          permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
      }

      JarvisTheme(palette = themePalette) {
        JarvisScreen(
          viewModel = viewModel,
          onRequestAudioPermission = { checkAndRequestAudioPermission() },
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
