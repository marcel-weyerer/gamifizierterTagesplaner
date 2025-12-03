package com.example.gamifiziertertagesplaner.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomAppBarOption(
  val icon: ImageVector,
  val tint: Color,
  val contentDescription: String,
  val onClick: () -> Unit
)
