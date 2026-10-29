package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SystemTelemetry

@Composable
fun TelemetryHudBar(
  telemetry: SystemTelemetry,
  continuousMode: Boolean,
  onToggleContinuous: () -> Unit,
  onToggleLanguage: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
  val blinkAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 900),
      repeatMode = RepeatMode.Reverse
    ),
    label = "blink_alpha"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(Color(0xFF040A14).copy(alpha = 0.9f))
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
      )
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // System Status
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(
              color = Color(0xFF00E676).copy(alpha = blinkAlpha),
              shape = CircleShape
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ONLINE",
          color = MaterialTheme.colorScheme.primary,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )
      }

      // Battery & Charging
      Text(
        text = "BAT: ${telemetry.batteryLevel}%${if (telemetry.isCharging) " ⚡" else ""}",
        color = if (telemetry.batteryLevel < 20) Color(0xFFFF1744) else MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace
      )

      // Language Switcher
      Box(
        modifier = Modifier
          .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            shape = RoundedCornerShape(4.dp)
          )
          .clickable(onClick = onToggleLanguage)
          .testTag("toggle_language_btn")
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = telemetry.languageMode,
          color = MaterialTheme.colorScheme.primary,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      // Continuous mode tag
      Box(
        modifier = Modifier
          .border(
            width = 1.dp,
            color = if (continuousMode) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
            shape = RoundedCornerShape(4.dp)
          )
          .clickable(onClick = onToggleContinuous)
          .testTag("toggle_continuous_mode_btn")
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = if (continuousMode) "AUTO-LOOP" else "TAP MODE",
          color = if (continuousMode) MaterialTheme.colorScheme.primary else Color.Gray,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}
