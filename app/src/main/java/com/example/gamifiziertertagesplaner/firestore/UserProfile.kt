package com.example.gamifiziertertagesplaner.firestore

import com.google.firebase.Timestamp

data class UserProfile(
  val uid: String = "",
  val username: String = "",
  val email: String = "",
  val userPoints: Int? = 0,
  val boughtBooks: Int = 0,
  val boughtPlants: Int = 0,
  val boughtDecoration: Int = 0,
  val photoUrl: String? = null,
  val createdAt: Timestamp = Timestamp.now()
)