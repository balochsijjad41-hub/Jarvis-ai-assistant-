package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PwaCodeExportSheet(
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  var selectedTab by remember { mutableIntStateOf(0) }

  // Load HTML from assets
  val htmlContent = remember {
    try {
      context.assets.open("index.html").bufferedReader().use { it.readText() }
    } catch (e: Exception) {
      "<!-- Could not load index.html -->"
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF060D19),
    contentColor = Color.White
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "JARVIS PWA WEB APPLICATION",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
          Text(
            text = "Single-file HTML/CSS/JS with Web Speech & PWA",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_pwa_sheet_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tab switcher: 0 = Interactive Web Preview, 1 = PWA Manifest & Source Code
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFF0B1728),
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              "WEBVIEW PREVIEW",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              "EXPORT HTML / PWA",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (selectedTab == 0) {
        // Interactive WebView running assets/index.html
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
        ) {
          AndroidView(
            modifier = Modifier.fillMaxWidth().height(420.dp),
            factory = { ctx ->
              WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.mediaPlaybackRequiresUserGesture = false
                webChromeClient = object : WebChromeClient() {
                  override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.grant(request.resources)
                  }
                }
                webViewClient = WebViewClient()
                loadUrl("file:///android_asset/index.html")
              }
            }
          )
        }
      } else {
        // Export & Setup Instructions
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF0E1A2C), RoundedCornerShape(8.dp))
              .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
              .padding(12.dp)
          ) {
            Column {
              Text(
                text = "PWA MOBILE INSTALLATION STEPS:",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "1. Save the code below as \"index.html\".\n" +
                  "2. Host on any HTTPS static host (GitHub Pages, Vercel, Netlify) or open in mobile Chrome/Safari.\n" +
                  "3. Tap the browser menu (⋮ on Android, Share on iOS) and select \"Add to Home Screen\".\n" +
                  "4. The embedded Service Worker & Web App Manifest will launch Jarvis full-screen like a native app!",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              clipboardManager.setText(AnnotatedString(htmlContent))
              Toast.makeText(context, "Full index.html copied to clipboard!", Toast.LENGTH_LONG).show()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = Color(0xFF030914)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("copy_html_code_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy HTML",
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
              "COPY FULL SINGLE-FILE HTML/CSS/JS",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .background(Color(0xFF040810), RoundedCornerShape(6.dp))
              .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Text(
              text = htmlContent.take(1200) + "\n\n... [${htmlContent.length} bytes total]",
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              lineHeight = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
