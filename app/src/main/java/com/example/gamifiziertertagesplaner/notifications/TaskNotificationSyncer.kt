package com.example.gamifiziertertagesplaner.notifications

import android.content.Context
import com.example.gamifiziertertagesplaner.firestore.Task

object TaskNotificationSyncer {

  /**
   * Sync one task:
   * - schedule if startTime != null and state != 0 and time is in the future
   * - else cancel
   */
  fun syncOne(context: Context, task: Task) {
    val start = task.startTime
    val isDone = task.state == 0

    // ---- start notification ----
    if (start == null || isDone) {
      TaskStartScheduler.cancelTaskStart(context, task.id)
    } else {
      val startMillis = start.toDate().time
      if (startMillis > System.currentTimeMillis()) {
        TaskStartScheduler.scheduleTaskStart(
          context = context,
          taskId = task.id,
          triggerAtMillis = startMillis,
          title = task.title,
          text = "Starte jetzt den Task"
        )
      } else {
        TaskStartScheduler.cancelTaskStart(context, task.id)
      }
    }

    // ---- reminder notification ----
    val reminderMinutes = task.reminder // Int? minutes before start
    if (start == null || isDone || reminderMinutes == null || reminderMinutes <= 0) {
      TaskReminderScheduler.cancelReminder(context, task.id)
      return
    }

    val reminderMillis = start.toDate().time - reminderMinutes.toLong() * 60_000L
    if (reminderMillis <= System.currentTimeMillis()) {
      // reminder time already passed -> don't schedule
      TaskReminderScheduler.cancelReminder(context, task.id)
      return
    }

    TaskReminderScheduler.scheduleReminder(
      context = context,
      taskId = task.id,
      triggerAtMillis = reminderMillis,
      title = task.title,
      text = "Startet in $reminderMinutes min ✏️"
    )
  }

  /**
   * Sync all tasks currently in memory, and cancel alarms for tasks no longer present.
   */
  fun syncAll(context: Context, tasks: List<Task>) {
    tasks.forEach { syncOne(context, it) }

    val currentIds = tasks.map { it.id }.toSet()

    TaskStartScheduler.getScheduledTaskIds(context)
      .filter { it !in currentIds }
      .forEach { TaskStartScheduler.cancelTaskStart(context, it) }

    TaskReminderScheduler.getScheduledTaskIds(context)
      .filter { it !in currentIds }
      .forEach { TaskReminderScheduler.cancelReminder(context, it) }
  }


  fun cancel(context: Context, task: Task) {
    TaskStartScheduler.cancelTaskStart(context, task.id)
  }
}
