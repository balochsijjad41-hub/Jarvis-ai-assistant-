package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.command.JarvisCommandEngine
import com.example.model.ActionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Jarvis AI", appName)
  }

  @Test
  fun `test command engine parses youtube command`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = JarvisCommandEngine(context)
    val result = engine.processCommand("Jarvis YouTube kholo", 85, false)

    assertEquals(ActionType.OPEN_APP, result.actionType)
    assertNotNull(result.externalIntent)
    assertTrue(result.replyText.contains("YouTube"))
  }

  @Test
  fun `test command engine parses battery status`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = JarvisCommandEngine(context)
    val result = engine.processCommand("battery kitni hai", 72, true)

    assertEquals(ActionType.BATTERY, result.actionType)
    assertTrue(result.replyText.contains("72"))
  }

  @Test
  fun `test command engine parses time command`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = JarvisCommandEngine(context)
    val result = engine.processCommand("time batao", 100, false)

    assertEquals(ActionType.TIME, result.actionType)
    assertTrue(result.replyText.contains("waqt"))
  }
}
