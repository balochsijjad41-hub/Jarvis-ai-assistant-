package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QuickActionItem(
  val label: String,
  val commandText: String,
  val icon: ImageVector,
  val testTag: String
)

@Composable
fun QuickActionHud(
  onCommandTriggered: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val shortcutsRow1 = listOf(
    QuickActionItem("YouTube", "youtube kholo", Icons.Filled.PlayCircle, "quick_youtube_btn"),
    QuickActionItem("Google", "google kholo", Icons.Filled.Language, "quick_google_btn"),
    QuickActionItem("WhatsApp", "whatsapp kholo", Icons.Filled.Send, "quick_whatsapp_btn"),
    QuickActionItem("Battery", "battery status", Icons.Filled.BatteryChargingFull, "quick_battery_btn")
  )

  val shortcutsRow2 = listOf(
    QuickActionItem("Time", "time batao", Icons.Filled.AccessTime, "quick_time_btn"),
    QuickActionItem("Date", "date batao", Icons.Filled.CalendarToday, "quick_date_btn"),
    QuickActionItem("Search", "search artificial intelligence", Icons.Filled.Search, "quick_search_btn"),
    QuickActionItem("Theme", "background badlo", Icons.Filled.ColorLens, "quick_theme_btn")
  )

  Column(modifier = modifier.fillMaxWidth()) {
    Text(
      text = "DIRECT PROTOCOL SHORTCUTS",
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 1.2.sp,
      modifier = Modifier.padding(bottom = 6.dp)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      shortcutsRow1.forEach { item ->
        QuickActionButton(
          item = item,
          onClick = { onCommandTriggered(item.commandText) },
          modifier = Modifier.weight(1f)
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      shortcutsRow2.forEach { item ->
        QuickActionButton(
          item = item,
          onClick = { onCommandTriggered(item.commandText) },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun QuickActionButton(
  item: QuickActionItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .background(
        color = Color(0xFF0B1728).copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp)
      )
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
        shape = RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onClick)
      .testTag(item.testTag)
      .padding(vertical = 8.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = item.icon,
        contentDescription = item.label,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = item.label,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace,
        maxLines = 1
      )
    }
  }
}
