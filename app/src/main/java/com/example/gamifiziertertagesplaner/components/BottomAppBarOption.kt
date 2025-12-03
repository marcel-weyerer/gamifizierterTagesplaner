package com.example.gamifiziertertagesplaner.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

data class BottomAppBarOption(
  val icon: Painter,
  val tint: Color,
  val contentDescription: String,
  val onClick: () -> Unit
)
