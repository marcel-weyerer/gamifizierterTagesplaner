package com.example.gamifiziertertagesplaner.util

import com.example.gamifiziertertagesplaner.firestore.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// Calculates task points based on priority and duration
fun calculateTaskPoints(priority: Int, durationMinutes: Int): Int {
  val basePoints = when (priority) {
    1 -> 20     // High
    2 -> 10      // Medium
    3 -> 5      // Low
    else -> 5
  }

  val pointsPerHour = 6
  val durationFactor = durationMinutes / 60.0
  val durationPoints = (durationFactor * pointsPerHour).roundToInt()

  return basePoints + durationPoints
}

// Accumulate total and received points of current task list
fun computePoints(tasks: List<Task>): PointsSummary {
  var total = 0
  var received = 0

  for (task in tasks) {
    val points = task.points
    total += points
    if (task.state == 0)
      received += points
  }

  return PointsSummary(total, received)
}

// Builds a time string for a task depending on the presence of start time and or duration
fun buildTimeString(task: Task): String {
  val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  var timeString = ""

  if (task.startTime != null) {
    timeString = dateFormat.format(task.startTime.toDate())

    if (task.duration != null) {
      val endTime = dateFormat.format(
        Date((task.startTime.toDate().time) + (task.duration * 60000L))
      )

      timeString += " - $endTime"
    } else {
      timeString += " Uhr"
    }
  } else if (task.duration != null) {
    timeString = String.format(
      Locale.getDefault(),
      "%02dh %02dmin",
      task.duration / 60,
      task.duration % 60
    )
  }

  return timeString
}