package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius

@Composable
fun CustomBottomAppBar(options: List<BottomAppBarOption>) {
  // Surface of BottomAppBar with rounded corners
  Surface(
    shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
    color = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary
  ) {
    BottomAppBar(
      modifier = Modifier
        .height(80.dp)
        .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)),
      containerColor = Color.Transparent
    ) {
      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        options.forEach { option ->
          IconButton(onClick = option.onClick) {
            Icon(
              modifier = Modifier.size(32.dp),
              painter = option.icon,
              contentDescription = option.contentDescription,
              tint = option.tint
            )
          }
        }
      }
    }
  }
}