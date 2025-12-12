package com.example.gamifiziertertagesplaner

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.gamifiziertertagesplaner.notifications.NotificationPermissionRequest
import com.example.gamifiziertertagesplaner.ui.theme.GamifizierterTagesplanerTheme

class MainActivity : ComponentActivity() {
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GamifizierterTagesplanerTheme {     // Use Tagesplaner theme
        NotificationPermissionRequest()
        App()
      }
    }

    createNotificationChannel()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        "daily_reminder_channel",
        "Daily task reminders",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Reminders to create your task list"
      }

      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(channel)
    }
  }
}