package com.example.gamifiziertertagesplaner.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.gamifiziertertagesplaner.MainActivity
import com.example.gamifiziertertagesplaner.R

class TaskReminderReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != TaskReminderScheduler.ACTION_TASK_REMINDER) return

    val taskId = intent.getStringExtra(TaskReminderScheduler.EXTRA_TASK_ID) ?: return
    val title = intent.getStringExtra(TaskReminderScheduler.EXTRA_TITLE) ?: "Erinnerung"
    val text = intent.getStringExtra(TaskReminderScheduler.EXTRA_TEXT) ?: "Aufgabe startet bald ✏️"

    // safety: ignore if it was canceled but still fired
    if (!TaskReminderScheduler.isTaskScheduled(context, taskId)) return

    showNotification(context, taskId, title, text)

    // one-shot cleanup
    TaskReminderScheduler.markFired(context, taskId)
  }

  private fun showNotification(context: Context, taskId: String, title: String, text: String) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      putExtra("open_task_id", taskId)
      addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      ("reminder:$taskId").hashCode(),
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, "daily_reminder_channel")
      .setSmallIcon(R.drawable.book)
      .setContentTitle(title)
      .setContentText(text)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()

    notificationManager.notify(("reminder:$taskId").hashCode(), notification)
  }
}
