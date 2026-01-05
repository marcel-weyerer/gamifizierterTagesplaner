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

    if (start == null || isDone) {
      TaskStartScheduler.cancelTaskStart(context, task.id)
      return
    }

    val triggerAtMillis = start.toDate().time
    if (triggerAtMillis <= System.currentTimeMillis()) {
      TaskStartScheduler.cancelTaskStart(context, task.id)
      return
    }

    TaskStartScheduler.scheduleTaskStart(
      context = context,
      taskId = task.id,
      triggerAtMillis = triggerAtMillis,
      title = "${task.title}",
      text = "Starte jetzt den Task."
    )
  }

  /**
   * Sync all tasks currently in memory, and cancel alarms for tasks no longer present.
   */
  fun syncAll(context: Context, tasks: List<Task>) {
    // 1) schedule/cancel for each current task
    tasks.forEach { syncOne(context, it) }

    // 2) cancel orphaned alarms (tasks deleted or user switched)
    // This requires TaskStartScheduler to expose the ids it has stored.
    val currentIds = tasks.map { it.id }.toSet()
    val scheduledIds = TaskStartScheduler.getScheduledTaskIds(context)

    val orphaned = scheduledIds.filter { it !in currentIds }
    orphaned.forEach { TaskStartScheduler.cancelTaskStart(context, it) }
  }

  fun cancel(context: Context, task: Task) {
    TaskStartScheduler.cancelTaskStart(context, task.id)
  }
}
