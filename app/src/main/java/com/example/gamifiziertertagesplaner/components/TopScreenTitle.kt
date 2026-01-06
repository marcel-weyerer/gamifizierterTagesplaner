package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown

/**
 * Reusable top screen title
 *
 * @param title  The title to display
 */
@Composable
fun TopScreenTitle(title: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(150.dp)
  ) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val radius = screenHeight / 2f

    // Draw circle for background and add screen title in front
    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            color = MediumBrown,
            radius = radius.toPx(),
            center = Offset(
              x = size.width / 2f,
              y = (-250).dp.toPx()
            )
          )
        }
    ) {
      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 32.dp),
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center
      )
    }
  }
}