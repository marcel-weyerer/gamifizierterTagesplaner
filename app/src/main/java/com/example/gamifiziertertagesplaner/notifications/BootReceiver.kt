package com.example.gamifiziertertagesplaner.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action ?: return

    if (
      action != Intent.ACTION_BOOT_COMPLETED &&
      action != Intent.ACTION_LOCKED_BOOT_COMPLETED
    ) return

    val (enabled, minutes) = DailyReminderScheduler.readSavedConfig(context)
    if (enabled) {
      DailyReminderScheduler.scheduleNextExact(context, minutes)
    }

    TaskStartScheduler.rescheduleAllFromPrefs(context)
  }
}

