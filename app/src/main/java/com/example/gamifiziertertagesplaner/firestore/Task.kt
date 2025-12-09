package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.Timestamp

data class Task(
  val id: String = "",
  val userId: String = "",
  val title: String = "",
  val priority: Int = 0,
  val description: String? = null,
  val startTime: Timestamp? = null,
  val duration: Int? = null,
  val reminder: Int? = null,
  val state: Int = 1,
  val points: Int = 0
)
