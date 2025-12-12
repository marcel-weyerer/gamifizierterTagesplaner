package com.example.gamifiziertertagesplaner.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Custom composable for the progress bar.
 * It shows the progress of the finished task by using the reveived points and the total points.
 *
 * @param receivedPoints  Number of received points from finished tasks.
 * @param totalPoints     Total number of points from all tasks of the day.
 */
@Composable
fun TaskProgressBar(
  receivedPoints: Int,
  totalPoints: Int
) {
  val targetProgress = if (totalPoints > 0) (receivedPoints.toFloat() / totalPoints) else 0f

  // Smooth animation between values
  val animatedProgress by animateFloatAsState(targetValue = targetProgress)

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 10.dp),
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation
  ) {
    LinearProgressIndicator(
      progress = { animatedProgress },
      modifier = Modifier
        .fillMaxWidth()
        .height(24.dp),
      color = MaterialTheme.colorScheme.onSecondary,
      trackColor = MaterialTheme.colorScheme.secondary
    )
  }
}