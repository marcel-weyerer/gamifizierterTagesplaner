package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.example.gamifiziertertagesplaner.ui.theme.Cream
import com.example.gamifiziertertagesplaner.ui.theme.DarkBrown
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown

/**
 * Circle background for the login screen, signup screen and end of day screen.
 */
@Composable
fun CircleBackground(maxHeight: Dp) {
  // Draw circles at different positions to create a gradient background
  Box(
    modifier = Modifier
      .fillMaxSize()
      .drawBehind {
        drawCircle(
          color = Cream,
          radius = (maxHeight / 3f).toPx(),
          center = Offset(x = size.width / 2f, y = size.height / 2f + 200)
        )
      }
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .drawBehind {
        drawCircle(
          color = MediumBrown,
          radius = (maxHeight / 3f).toPx(),
          center = Offset(x = size.width / 2f, y = size.height / 3f + 175)
        )
      }
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .drawBehind {
        drawCircle(
          color = DarkBrown,
          radius = (maxHeight / 3f).toPx(),
          center = Offset(x = size.width / 2f, y = 0f)
        )
      }
  )
}