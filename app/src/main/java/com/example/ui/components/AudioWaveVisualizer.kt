package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun AudioWaveVisualizer(
  isListening: Boolean,
  rmsDb: Float,
  color: Color = MaterialTheme.colorScheme.primary,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "audio_wave_transition")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.283f, // 2 * PI
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (isListening) 900 else 2500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(48.dp)
      .background(Color(0xFF07111F).copy(alpha = 0.7f), RoundedCornerShape(8.dp))
      .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = if (isListening) "VOICE FREQUENCY DETECTOR" else "AUDIO SENSOR STANDBY",
      color = color.copy(alpha = 0.6f),
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.sp,
      modifier = Modifier.align(Alignment.TopStart)
    )

    Canvas(modifier = Modifier.fillMaxWidth().height(32.dp).align(Alignment.BottomCenter)) {
      val barCount = 28
      val totalWidth = size.width
      val barSpacing = 4f
      val barWidth = (totalWidth - (barCount - 1) * barSpacing) / barCount
      val centerY = size.height / 2f
      val maxHalfHeight = size.height * 0.45f

      for (i in 0 until barCount) {
        val x = i * (barWidth + barSpacing)
        val normalizedIndex = i.toFloat() / barCount

        val rawSine = sin(wavePhase + normalizedIndex * 4f)
        val secondHarmonic = sin(wavePhase * 2f + normalizedIndex * 8f) * 0.5f

        val heightFactor = if (isListening) {
          val audioBoost = (rmsDb / 8f).coerceIn(0.1f, 1.2f)
          (abs(rawSine + secondHarmonic) * 0.7f * audioBoost + 0.15f).coerceIn(0.08f, 1f)
        } else {
          (abs(rawSine) * 0.2f + 0.05f)
        }

        val barHeight = maxHalfHeight * 2f * heightFactor
        val top = centerY - barHeight / 2f

        drawRoundRect(
          color = if (isListening) color else color.copy(alpha = 0.3f),
          topLeft = Offset(x, top),
          size = Size(barWidth, barHeight),
          cornerRadius = CornerRadius(2f, 2f)
        )
      }
    }
  }
}
