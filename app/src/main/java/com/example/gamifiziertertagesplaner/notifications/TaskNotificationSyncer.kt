package com.example.gamifiziertertagesplaner.notifications

import android.content.Context
import com.example.gamifiziertertagesplaner.firestore.Task

object TaskNotificationSyncer {
  fun syncOne(context: Context, task: Task) {
    val start = task.startTime
    val isDone = task.state == 0

    // Task start time notification
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

    // Task reminder notification
    val reminderMinutes = task.reminder
    if (start == null || isDone || reminderMinutes == null || reminderMinutes <= 0) {
      TaskReminderScheduler.cancelReminder(context, task.id)
      return
    }

    val reminderMillis = start.toDate().time - reminderMinutes.toLong() * 60_000L

    // If reminder time has already passed, don't schedule an alarm
    if (reminderMillis <= System.currentTimeMillis()) {
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
