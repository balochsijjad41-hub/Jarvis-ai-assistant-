package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MessageSender
import com.example.model.VoiceMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TranscriptCard(
  messages: List<VoiceMessage>,
  onReplaySpeech: (String) -> Unit,
  onClearHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(
        color = Color(0xFF091424).copy(alpha = 0.85f),
        shape = RoundedCornerShape(12.dp)
      )
      .border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
      )
      .padding(12.dp)
  ) {
    Column {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "TRANSMISSION LOG",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp
          )
        }

        if (messages.isNotEmpty()) {
          Text(
            text = "CLEAR LOG",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
              .clickable(onClick = onClearHistory)
              .testTag("clear_log_btn")
              .padding(4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Messages List
      if (messages.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No voice transmissions recorded.\nTap Arc Reactor or say \"Jarvis\" to begin.",
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
          )
        }
      } else {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(messages, key = { it.id }) { msg ->
            TranscriptItem(
              message = msg,
              onReplay = { onReplaySpeech(msg.text) },
              onCopy = {
                clipboardManager.setText(AnnotatedString(msg.text))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TranscriptItem(
  message: VoiceMessage,
  onReplay: () -> Unit,
  onCopy: () -> Unit
) {
  val isUser = message.sender == MessageSender.USER
  val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
  val timeStr = timeFormat.format(Date(message.timestamp))

  val accentColor = if (isUser) Color(0xFFFFD700) else MaterialTheme.colorScheme.primary
  val bgBubble = if (isUser) Color(0x15FFD700) else Color(0x1500F0FF)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .background(bgBubble, RoundedCornerShape(6.dp))
      .border(
        width = 1.dp,
        color = accentColor.copy(alpha = 0.35f),
        shape = RoundedCornerShape(6.dp)
      )
      .padding(8.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isUser) "COMMAND // BOSS" else "RESPONSE // JARVIS",
          color = accentColor,
          fontSize = 9.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = timeStr,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace
          )
          Spacer(modifier = Modifier.width(4.dp))
          IconButton(
            onClick = onCopy,
            modifier = Modifier.size(20.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy message",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.size(12.dp)
            )
          }
          if (!isUser) {
            IconButton(
              onClick = onReplay,
              modifier = Modifier.size(20.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Speak response",
                tint = accentColor,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = message.text,
        color = Color(0xFFF0FDFE),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
      )
    }
  }
}
