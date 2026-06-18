package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      Column(
          modifier = Modifier.background(Color(0xFF0F1116)).padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
          com.example.ui.ThreeStateButton(
              status = 0,
              primaryCyan = Color(0xFF0072FF),
              accentPink = Color(0xFFFF007F),
              textMuted = Color(0xFF64748B),
              onCycle = {}
          )
          com.example.ui.ThreeStateButton(
              status = 1,
              primaryCyan = Color(0xFF0072FF),
              accentPink = Color(0xFFFF007F),
              textMuted = Color(0xFF64748B),
              onCycle = {}
          )
          com.example.ui.ThreeStateButton(
              status = 2,
              primaryCyan = Color(0xFF0072FF),
              accentPink = Color(0xFFFF007F),
              textMuted = Color(0xFF64748B),
              onCycle = {}
          )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
