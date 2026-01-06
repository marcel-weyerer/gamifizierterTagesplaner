package com.example.gamifiziertertagesplaner.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/**
 * Data class that represents a bottom app bar icon
 */
data class BottomAppBarOption(
  val icon: Painter,
  val tint: Color,
  val contentDescription: String,
  val onClick: () -> Unit
)
