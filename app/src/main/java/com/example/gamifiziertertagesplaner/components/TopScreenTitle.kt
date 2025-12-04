package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown

@Composable
fun TopScreenTitle(
  innerPadding: PaddingValues,
  title: String
) {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)
      .offset(y = (-200).dp)
  ) {
    val halfHeight = maxHeight / 2f

    // Circle background
    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            color = MediumBrown,
            radius = halfHeight.toPx(),
            center = Offset(x = size.width / 2f, y = 0f)
          )
        }
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 225.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onPrimary
        )
      }
    }
  }
}