package com.example.gamifiziertertagesplaner.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.edit

object TaskStartScheduler {
  const val ACTION_TASK_START = "com.example.gamifiziertertagesplaner.TASK_START"

  const val EXTRA_TASK_ID = "extra_task_id"
  const val EXTRA_TITLE = "extra_title"
  const val EXTRA_TEXT = "extra_text"

  private const val PREFS = "task_start_prefs"
  private const val KEY_SET = "scheduled_tasks" // StringSet of encoded entries

  // Encode in a delimiter-safe way
  private fun encode(taskId: String, triggerAtMillis: Long, title: String, text: String): String {
    // Very small escaping to keep it stable
    fun esc(s: String) = s.replace("\\", "\\\\").replace("|", "\\|")
    return "${esc(taskId)}|$triggerAtMillis|${esc(title)}|${esc(text)}"
  }

  private fun decode(entry: String): Decoded? {
    // Split on unescaped |
    val parts = mutableListOf<String>()
    val sb = StringBuilder()
    var escaped = false
    for (c in entry) {
      when {
        escaped -> { sb.append(c); escaped = false }
        c == '\\' -> escaped = true
        c == '|' -> { parts.add(sb.toString()); sb.setLength(0) }
        else -> sb.append(c)
      }
    }
    parts.add(sb.toString())

    if (parts.size < 4) return null

    val taskId = parts[0]
    val triggerAt = parts[1].toLongOrNull() ?: return null
    val title = parts[2]
    val text = parts[3]
    return Decoded(taskId, triggerAt, title, text)
  }

  private data class Decoded(
    val taskId: String,
    val triggerAtMillis: Long,
    val title: String,
    val text: String
  )

  fun scheduleTaskStart(
    context: Context,
    taskId: String,
    triggerAtMillis: Long,
    title: String,
    text: String
  ) {
    // If time is in the past, don’t schedule.
    if (triggerAtMillis <= System.currentTimeMillis()) {
      cancelTaskStart(context, taskId)
      return
    }

    upsertLocal(context, taskId, triggerAtMillis, title, text)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = buildPendingIntent(context, taskId, title, text)

    // Cancel old alarm first
    alarmManager.cancel(pendingIntent)

    scheduleExact(alarmManager, triggerAtMillis, pendingIntent)
  }

  fun cancelTaskStart(context: Context, taskId: String) {
    removeLocal(context, taskId)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = buildPendingIntent(context, taskId, title = "x", text = "x")
    alarmManager.cancel(pendingIntent)
  }

  fun rescheduleAllFromPrefs(context: Context) {
    val now = System.currentTimeMillis()
    val entries = getAllLocal(context)

    entries.forEach { d ->
      // Only reschedule future alarms
      if (d.triggerAtMillis > now) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, d.taskId, d.title, d.text)
        alarmManager.cancel(pendingIntent)
        scheduleExact(alarmManager, d.triggerAtMillis, pendingIntent)
      } else {
        // Past -> cleanup
        removeLocal(context, d.taskId)
      }
    }
  }

  fun isTaskScheduled(context: Context, taskId: String): Boolean {
    return getAllLocal(context).any { it.taskId == taskId }
  }

  fun markFired(context: Context, taskId: String) {
    removeLocal(context, taskId)
  }

  // Helper functions

  private fun buildPendingIntent(
    context: Context,
    taskId: String,
    title: String,
    text: String
  ): PendingIntent {
    val intent = Intent(context, TaskStartReceiver::class.java).apply {
      action = ACTION_TASK_START
      putExtra(EXTRA_TASK_ID, taskId)
      putExtra(EXTRA_TITLE, title)
      putExtra(EXTRA_TEXT, text)
    }

    return PendingIntent.getBroadcast(
      context,
      taskId.hashCode(), // unique per task
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
  }

  private fun scheduleExact(
    alarmManager: AlarmManager,
    triggerAtMillis: Long,
    pendingIntent: PendingIntent
  ) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        return
      }
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    } catch (se: SecurityException) {
      Log.w("TaskStartScheduler", "Exact alarms not allowed, falling back.", se)
      alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
  }

  private fun prefs(context: Context) =
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

  private fun getAllLocal(context: Context): List<Decoded> {
    val set = prefs(context).getStringSet(KEY_SET, emptySet()) ?: emptySet()
    return set.mapNotNull { decode(it) }
  }

  private fun upsertLocal(
    context: Context,
    taskId: String,
    triggerAtMillis: Long,
    title: String,
    text: String
  ) {
    val encoded = encode(taskId, triggerAtMillis, title, text)
    val existing = prefs(context).getStringSet(KEY_SET, emptySet())?.toMutableSet() ?: mutableSetOf()

    // remove any existing entry for that taskId
    existing.removeAll { decode(it)?.taskId == taskId }
    existing.add(encoded)

    prefs(context).edit { putStringSet(KEY_SET, existing) }
  }

  private fun removeLocal(context: Context, taskId: String) {
    val existing = prefs(context).getStringSet(KEY_SET, emptySet())?.toMutableSet() ?: mutableSetOf()
    val changed = existing.removeAll { decode(it)?.taskId == taskId }
    if (changed) {
      prefs(context).edit { putStringSet(KEY_SET, existing) }
    }
  }

  fun getScheduledTaskIds(context: Context): Set<String> {
    return getAllLocal(context).map { it.taskId }.toSet()
  }
}