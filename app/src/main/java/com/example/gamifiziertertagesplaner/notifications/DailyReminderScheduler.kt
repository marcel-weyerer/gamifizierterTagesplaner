package com.example.gamifiziertertagesplaner.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import java.util.Calendar

object DailyReminderScheduler {

  private const val REQUEST_CODE = 42
  private const val ACTION_DAILY_REMINDER = "com.example.gamifiziertertagesplaner.DAILY_REMINDER"

  private const val PREFS = "daily_reminder_prefs"
  private const val KEY_ENABLED = "enabled"
  private const val KEY_MINUTES = "minutesSinceMidnight"

  fun scheduleDailyReminder(context: Context, minutesSinceMidnight: Int) {
    // Persist so BootReceiver and Receiver can reschedule correctly
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
      putBoolean(KEY_ENABLED, true)
      putInt(KEY_MINUTES, minutesSinceMidnight)
    }

    // Schedule next occurrence
    scheduleNextExact(context, minutesSinceMidnight)
  }

  // Schedules the next occurrence
  fun scheduleNextExact(context: Context, minutesSinceMidnight: Int) {
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

    val now = Calendar.getInstance()
    val triggerTime = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, minutesSinceMidnight / 60)
      set(Calendar.MINUTE, minutesSinceMidnight % 60)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
      if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
    }

    // Cancel any previous scheduled alarm
    alarmManager.cancel(pendingIntent)

    scheduleExact(alarmManager, triggerTime.timeInMillis, pendingIntent)
  }

  private fun scheduleExact(
    alarmManager: AlarmManager,
    triggerAtMillis: Long,
    pendingIntent: PendingIntent
  ) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        // Fallback
        alarmManager.setAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          triggerAtMillis,
          pendingIntent
        )
        return
      }

      alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
      )
    } catch (se: SecurityException) {
      Log.w("DailyReminder", "Exact alarms not allowed, falling back.", se)
      alarmManager.setAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
      )
    }
  }

  fun cancelDailyReminder(context: Context) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
      putBoolean(KEY_ENABLED, false)
    }

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

  // Helper function to read saved config
  fun readSavedConfig(context: Context): Pair<Boolean, Int> {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_ENABLED, false) to prefs.getInt(KEY_MINUTES, 18 * 60)
  }

  fun isEnabled(context: Context): Boolean =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
      .getBoolean(KEY_ENABLED, false)

  fun savedMinutes(context: Context): Int =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
      .getInt(KEY_MINUTES, 18 * 60)

}
