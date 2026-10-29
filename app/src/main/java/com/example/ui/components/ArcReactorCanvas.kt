package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ArcReactorCore(
  isListening: Boolean,
  isSpeaking: Boolean,
  rmsDb: Float,
  glowColor: Color = MaterialTheme.colorScheme.primary,
  onToggleListen: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "arc_reactor_animation")

  // Clockwise outer rotation
  val outerRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (isListening) 6000 else 24000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "outer_rotation"
  )

  // Counter-clockwise inner rotation
  val innerRotation by infiniteTransition.animateFloat(
    initialValue = 360f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (isListening) 4000 else 16000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "inner_rotation"
  )

  // Core energy pulsing
  val corePulse by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = if (isListening) 700 else 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "core_pulse"
  )

  val dynamicScale = if (isListening) {
    corePulse + (rmsDb / 40f).coerceIn(0f, 0.25f)
  } else {
    corePulse
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .size(230.dp)
      .testTag("arc_reactor_button")
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = false, radius = 120.dp),
        onClick = onToggleListen
      )
  ) {
    Canvas(modifier = Modifier.size(230.dp)) {
      val center = Offset(size.width / 2f, size.height / 2f)
      val radius = min(size.width, size.height) / 2f

      // Outer dashed tracking ring
      rotate(outerRotation, pivot = center) {
        drawCircle(
          color = glowColor.copy(alpha = 0.35f),
          radius = radius * 0.94f,
          center = center,
          style = Stroke(
            width = 2.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 16f, 8f, 16f), 0f)
          )
        )
      }

      // Mid segmented power arc ring
      rotate(innerRotation, pivot = center) {
        val segmentCount = 10
        val sweepAngle = 360f / segmentCount
        for (i in 0 until segmentCount) {
          val startAngle = i * sweepAngle + 4f
          drawArc(
            color = if (i % 2 == 0) glowColor else glowColor.copy(alpha = 0.45f),
            startAngle = startAngle,
            sweepAngle = sweepAngle - 8f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.78f, center.y - radius * 0.78f),
            size = Size(radius * 1.56f, radius * 1.56f),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
          )
        }
      }

      // Inner notch ring with power teeth
      val teethCount = 16
      val toothRadius = radius * 0.62f
      for (i in 0 until teethCount) {
        val angleRad = Math.toRadians((i * (360.0 / teethCount) + outerRotation * 0.5).toDouble())
        val startX = center.x + (toothRadius - 6f) * cos(angleRad).toFloat()
        val startY = center.y + (toothRadius - 6f) * sin(angleRad).toFloat()
        val endX = center.x + toothRadius * cos(angleRad).toFloat()
        val endY = center.y + toothRadius * sin(angleRad).toFloat()

        drawLine(
          color = glowColor.copy(alpha = 0.6f),
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = 3f,
          cap = StrokeCap.Round
        )
      }

      // Energy glow outer aura
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            glowColor.copy(alpha = if (isListening) 0.55f else 0.25f),
            glowColor.copy(alpha = 0.08f),
            Color.Transparent
          ),
          center = center,
          radius = radius * 0.85f * dynamicScale
        ),
        radius = radius * 0.85f * dynamicScale,
        center = center
      )

      // Solid central reactor chamber
      val coreRadius = radius * 0.42f * dynamicScale
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color.White,
            glowColor,
            glowColor.copy(alpha = 0.8f),
            Color(0xFF050B14)
          ),
          center = center,
          radius = coreRadius
        ),
        radius = coreRadius,
        center = center
      )

      // Core border outline
      drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = coreRadius,
        center = center,
        style = Stroke(width = 2.5f)
      )
    }

    // Center icon & status label
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.align(Alignment.Center)
    ) {
      Icon(
        imageVector = if (isListening) Icons.Filled.Mic else Icons.Filled.MicOff,
        contentDescription = if (isListening) "Jarvis Listening" else "Jarvis Idle",
        tint = Color(0xFF030914),
        modifier = Modifier.size(34.dp)
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = when {
          isSpeaking -> "SPEAKING"
          isListening -> "LISTENING"
          else -> "STANDBY"
        },
        color = Color(0xFF030914),
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.2.sp
      )
    }
  }
}
