package com.example.gamifiziertertagesplaner.feature.endOfDay

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.CircleBackground
import com.example.gamifiziertertagesplaner.components.TaskProgressBar
import com.example.gamifiziertertagesplaner.feature.home.HomeViewModel
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.util.Date

/**
 * End of day screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EndOfDayScreen(
  homeViewModel: HomeViewModel,
  authViewModel: AuthViewModel,
  onOpenHome: () -> Unit
) {
  // Received and total points used for progress bar
  val totalPoints by homeViewModel.totalPoints.collectAsState()
  val receivedPoints by homeViewModel.receivedPoints.collectAsState()
  val totalPointsText = "$receivedPoints Punkte verdient"

  // Number of all tasks and done tasks
  val allTasks = homeViewModel.getTaskCount()
  val doneTasks = homeViewModel.getDoneTasks()
  val tasksText = "$doneTasks von $allTasks Tasks erledigt"

  // Total user points
  val userPoints = (authViewModel.userProfile?.userPoints ?: 0) + receivedPoints

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White),
  ) {
    CircleBackground(maxHeight)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 48.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Tag\nbeendet!",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onPrimary,
          textAlign = TextAlign.Center,
        )
      }

      Spacer(modifier = Modifier.height(100.dp))

      // Amount of done tasks
      Text(
        text = tasksText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimary,
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Progress in progress bar
      TaskProgressBar(
        receivedPoints = receivedPoints,
        totalPoints = totalPoints
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Total points received
      Text(
        text = totalPointsText,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onPrimary,
      )

      Spacer(modifier = Modifier.height(48.dp))

      // New total user points
      Text(
        text = "Neuer Punktestand",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimary,
      )

      Text(
        text = userPoints.toString(),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onPrimary,
      )

      Spacer(modifier = Modifier.height(65.dp))

      // Reset end of day time and revert to home screen
      ActionButton(
        onClick = {
          val endOfDayTime = authViewModel.userProfile?.endOfDayTime

          val newEndOfDayTime = endOfDayTime?.let { ts ->
            // Convert Timestamp to LocalDateTime
            val currentLdt = ts.toDate()
              .toInstant()
              .atZone(ZoneId.systemDefault())
              .toLocalDateTime()

            // Same time next day
            val nextDayLdt = currentLdt.plusDays(1)

            // Convert back to Timestamp
            val nextInstant = nextDayLdt
              .atZone(ZoneId.systemDefault())
              .toInstant()

            Timestamp(Date.from(nextInstant))
          }

          // Set same time next day
          if (newEndOfDayTime != null)
            authViewModel.updateEndOfDay(newEndOfDayTime)

          // Update user points, delete done tasks and open home screen
          authViewModel.updateUserPoints(userPoints)
          homeViewModel.deleteDoneTasks()
          onOpenHome()
        },
        modifier = Modifier.width(200.dp),
        text = "Tag beenden"
      )
    }
  }
}