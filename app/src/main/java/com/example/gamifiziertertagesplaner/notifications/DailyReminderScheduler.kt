package com.example.gamifiziertertagesplaner.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object DailyReminderScheduler {

  private const val REQUEST_CODE = 42
  private const val ACTION_DAILY_REMINDER = "com.example.gamifiziertertagesplaner.DAILY_REMINDER"

  fun scheduleDailyReminder(context: Context, minutesSinceMidnight: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, DailyReminderReceiver::class.java).apply {
      action = ACTION_DAILY_REMINDER
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Compute first trigger time (today or tomorrow)
    val now = Calendar.getInstance()
    val triggerTime = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, minutesSinceMidnight / 60)
      set(Calendar.MINUTE, minutesSinceMidnight % 60)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)

      if (before(now)) {
        add(Calendar.DAY_OF_YEAR, 1) // if time already passed today → tomorrow
      }
    }

    val triggerMillis = triggerTime.timeInMillis

    // Repeating daily alarm
    alarmManager.setRepeating(
      AlarmManager.RTC_WAKEUP,
      triggerMillis,
      AlarmManager.INTERVAL_DAY,
      pendingIntent
    )
  }

  fun cancelDailyReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, DailyReminderReceiver::class.java).apply {
      action = ACTION_DAILY_REMINDER
    }

    val pendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    alarmManager.cancel(pendingIntent)
  }
}
