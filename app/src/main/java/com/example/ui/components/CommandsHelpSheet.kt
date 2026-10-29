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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CommandProtocol(
  val voicePhrase: String,
  val urduPhrase: String,
  val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandsHelpSheet(
  onDismiss: () -> Unit,
  onSelectCommand: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val protocols = listOf(
    CommandProtocol(
      voicePhrase = "Jarvis YouTube kholo",
      urduPhrase = "یوٹیوب کھولو",
      description = "Opens YouTube in your app or browser."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis Google kholo",
      urduPhrase = "گوگل کھولو",
      description = "Launches Google search engine homepage."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis WhatsApp kholo",
      urduPhrase = "واٹس ایپ کھولو",
      description = "Opens WhatsApp messenger directly."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis time batao / Waqt kya hua hai",
      urduPhrase = "وقت کیا ہوا ہے",
      description = "Speaks and displays current local time in Roman Urdu & English."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis date batao / Aaj ki tareekh",
      urduPhrase = "آج کی تاریخ",
      description = "Announces today's full date and day."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis battery status / Battery kitni hai",
      urduPhrase = "بیٹری کی حالت",
      description = "Reads device battery percentage and charging state."
    ),
    CommandProtocol(
      voicePhrase = "Search [query] / [query] search karo",
      urduPhrase = "گوگل پر تلاش کرو",
      description = "Directly performs a Google web search for your query."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis background badlo / Change theme",
      urduPhrase = "تھیم بدلو",
      description = "Cycles Arc Reactor core colors: Cyan, Gold, Emerald, Violet."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis system status / Diagnostic",
      urduPhrase = "سسٹم رپورٹ",
      description = "Runs complete hardware and neural voice engine diagnostic."
    ),
    CommandProtocol(
      voicePhrase = "Jarvis / Suno Jarvis / Hello",
      urduPhrase = "سنو جاروِس",
      description = "Wake greetings; Jarvis acknowledges and stands ready."
    )
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF070F1C),
    contentColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "JARVIS PROTOCOL COMMANDS",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp
          )
          Text(
            text = "Tap any command to test or speak it aloud",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_commands_sheet_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
      ) {
        items(protocols) { p ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0C192E), RoundedCornerShape(8.dp))
              .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
              .clickable {
                onSelectCommand(p.voicePhrase)
                onDismiss()
              }
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "“${p.voicePhrase}”",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = p.urduPhrase,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = p.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 11.sp,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
