package com.example.command

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.model.ActionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CommandResult(
  val replyText: String,
  val actionType: ActionType,
  val externalIntent: Intent? = null,
  val triggerThemeChange: Boolean = false
)

class JarvisCommandEngine(private val context: Context) {

  fun processCommand(
    rawText: String,
    batteryLevel: Int,
    isCharging: Boolean
  ): CommandResult {
    val clean = rawText.trim().lowercase(Locale.ROOT)

    // 1. YouTube
    if (clean.contains("youtube") || clean.contains("یوٹیوب")) {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Opening YouTube for you, Boss.",
        actionType = ActionType.OPEN_APP,
        externalIntent = intent
      )
    }

    // 2. Google
    if (clean.contains("google") || clean.contains("گوگل")) {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Opening Google Search, Boss.",
        actionType = ActionType.OPEN_APP,
        externalIntent = intent
      )
    }

    // 3. WhatsApp
    if (clean.contains("whatsapp") || clean.contains("واٹس ایپ")) {
      val launchIntent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
      val intent = launchIntent ?: Intent(Intent.ACTION_VIEW, Uri.parse("https://web.whatsapp.com")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Launching WhatsApp, Boss.",
        actionType = ActionType.OPEN_APP,
        externalIntent = intent
      )
    }

    // 4. Facebook
    if (clean.contains("facebook") || clean.contains("فیس بک")) {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Opening Facebook, Boss.",
        actionType = ActionType.OPEN_APP,
        externalIntent = intent
      )
    }

    // 5. Instagram
    if (clean.contains("instagram") || clean.contains("انسٹاگرام")) {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Opening Instagram, Boss.",
        actionType = ActionType.OPEN_APP,
        externalIntent = intent
      )
    }

    // 6. Wikipedia
    if (clean.contains("wikipedia") || clean.contains("وکی پیڈیا")) {
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wikipedia.org")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Accessing Wikipedia archives, Boss.",
        actionType = ActionType.OPEN_APP,
        externalIntent = intent
      )
    }

    // 7. Time
    if (clean.contains("time") || clean.contains("waqt") || clean.contains("وقت") || clean.contains("kitne baje")) {
      val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
      val currentTime = timeFormat.format(Date())
      return CommandResult(
        replyText = "Boss, abhi waqt $currentTime ho raha hai.",
        actionType = ActionType.TIME
      )
    }

    // 8. Date
    if (clean.contains("date") || clean.contains("tareekh") || clean.contains("تاریخ") || clean.contains("aaj kya")) {
      val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
      val currentDate = dateFormat.format(Date())
      return CommandResult(
        replyText = "Boss, aaj ki tareekh $currentDate hai.",
        actionType = ActionType.DATE
      )
    }

    // 9. Battery Status
    if (clean.contains("battery") || clean.contains("بیٹری") || clean.contains("charge") || clean.contains("power")) {
      val chargeStatus = if (isCharging) "aur charger plugged in hai" else "aur discharging state mein hai"
      return CommandResult(
        replyText = "Boss, battery $batteryLevel percent hai $chargeStatus.",
        actionType = ActionType.BATTERY
      )
    }

    // 10. Change Theme / Background
    if (clean.contains("background") || clean.contains("theme") || clean.contains("color") || clean.contains("badlo") || clean.contains("بدلو")) {
      return CommandResult(
        replyText = "Switching Arc Reactor power spectrum, Boss.",
        actionType = ActionType.THEME_CHANGE,
        triggerThemeChange = true
      )
    }

    // 11. Search Command
    if (clean.startsWith("search") || clean.contains("search karo") || clean.contains("dhoondo") || clean.contains("تلاش")) {
      val query = clean
        .replace("jarvis", "")
        .replace("search karo", "")
        .replace("search", "")
        .replace("dhoondo", "")
        .replace("google per", "")
        .trim()

      val searchQuery = if (query.isEmpty()) "AI news" else query
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(searchQuery)}")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      return CommandResult(
        replyText = "Searching Google for $searchQuery, Boss.",
        actionType = ActionType.SEARCH,
        externalIntent = intent
      )
    }

    // 12. System Diagnostic
    if (clean.contains("diagnostic") || clean.contains("system status") || clean.contains("halat") || clean.contains("حالت")) {
      return CommandResult(
        replyText = "All systems operational, Boss. Arc Reactor core at 100%, battery at $batteryLevel percent, neural speech engine active.",
        actionType = ActionType.DIAGNOSTIC
      )
    }

    // 13. Help / Madad
    if (clean.contains("help") || clean.contains("madad") || clean.contains("مدد") || clean.contains("kya kar sakte")) {
      return CommandResult(
        replyText = "I can open YouTube, Google, WhatsApp, check time, date, battery status, search web queries, and switch Arc Reactor themes. Just speak your command.",
        actionType = ActionType.HELP
      )
    }

    // 14. Wake word / Greetings
    if (clean.contains("jarvis") || clean.contains("hello") || clean.contains("suno") || clean.contains("hi") || clean.contains("salam")) {
      val replies = listOf(
        "Yes Boss, Jarvis online and ready for your command.",
        "Jee Boss, Jarvis hazir hai. Hukum kijiye.",
        "Always at your service, Boss. What do you need?",
        "Sub-systems primed. Awaiting your instructions, Boss."
      )
      return CommandResult(
        replyText = replies.random(),
        actionType = ActionType.GREETING
      )
    }

    // Fallback: Perform web search for arbitrary command
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(rawText)}")).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return CommandResult(
      replyText = "Command recognized: \"$rawText\". Searching web for further information, Boss.",
      actionType = ActionType.SEARCH,
      externalIntent = intent
    )
  }
}
