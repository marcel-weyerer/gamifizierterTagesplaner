package com.example.gamifiziertertagesplaner.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.gamifiziertertagesplaner.MainActivity
import com.example.gamifiziertertagesplaner.R

/**
 * Receiver for the daily reminder notification.
 */
class DailyReminderReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != "com.example.gamifiziertertagesplaner.DAILY_REMINDER") return

    showDailyReminderNotification(context)

    // schedule tomorrow (exact) after firing
    val (enabled, minutes) = DailyReminderScheduler.readSavedConfig(context)
    if (enabled) {
      DailyReminderScheduler.scheduleNextExact(context, minutes)
    }
  }


  private fun showDailyReminderNotification(context: Context) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val openAppIntent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "daily_reminder_channel")
      .setSmallIcon(R.drawable.book)
      .setContentTitle("Zeit für deine Tagesplanung")
      .setContentText("Erstelle jetzt deine Aufgabenliste für morgen ✏️")
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(1001, notification)
  }
}
