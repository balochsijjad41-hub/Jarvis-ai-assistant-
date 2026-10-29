package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class JarvisThemePalette(val label: String, val primary: Color, val glow: Color, val secondary: Color) {
  ARC_CYAN("Arc Cyan", JarvisCyan, JarvisCyanGlow, JarvisBlueCore),
  MARK_VII("Stark Gold", StarkGold, StarkGoldGlow, StarkCrimson),
  STEALTH("Stealth Emerald", StealthGreen, StealthGreenGlow, Color(0xFF00B0FF)),
  PLASMA("Plasma Violet", PlasmaViolet, PlasmaVioletGlow, Color(0xFF651FFF))
}

@Composable
fun JarvisTheme(
  palette: JarvisThemePalette = JarvisThemePalette.ARC_CYAN,
  content: @Composable () -> Unit
) {
  val colorScheme = darkColorScheme(
    primary = palette.primary,
    onPrimary = Color(0xFF02070D),
    primaryContainer = JarvisCyanDark,
    onPrimaryContainer = palette.primary,
    secondary = palette.secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF002233),
    onSecondaryContainer = palette.primary,
    tertiary = JarvisBlueCore,
    background = JarvisDarkNavy,
    onBackground = JarvisTextPrimary,
    surface = JarvisDeepSurface,
    onSurface = JarvisTextPrimary,
    surfaceVariant = JarvisCardSurface,
    onSurfaceVariant = JarvisTextSecondary,
    outline = palette.primary.copy(alpha = 0.5f),
    outlineVariant = JarvisCardBorder
  )

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
