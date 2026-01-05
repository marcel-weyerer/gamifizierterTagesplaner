package com.example.gamifiziertertagesplaner.feature.settings

import androidx.compose.runtime.Composable

enum class SettingsType {
  USERNAME,
  PROFILE_PICTURE,
  PASSWORD,
  EMAIL,
  NOTIFICATION,
  ENDOFDAY
}

data class SettingsContent(
  val title: String,
  val description: String,
  val content: @Composable () -> Unit
)