package com.example.gamifiziertertagesplaner.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.gamifiziertertagesplaner.MainActivity
import com.example.gamifiziertertagesplaner.R

class TaskStartReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != TaskStartScheduler.ACTION_TASK_START) return

    val taskId = intent.getStringExtra(TaskStartScheduler.EXTRA_TASK_ID) ?: return
    val title = intent.getStringExtra(TaskStartScheduler.EXTRA_TITLE) ?: "Aufgabe startet"
    val text = intent.getStringExtra(TaskStartScheduler.EXTRA_TEXT) ?: "Los geht’s ✏️"

    showTaskStartNotification(context, taskId, title, text)

    // Remove it from local storage
    TaskStartScheduler.markFired(context, taskId)
  }

  private fun showTaskStartNotification(
    context: Context,
    taskId: String,
    title: String,
    text: String
  ) {
    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      putExtra("open_task_id", taskId)
      addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      taskId.hashCode(),
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

    notificationManager.notify(taskId.hashCode(), notification)
  }
}
