package com.example.gamifiziertertagesplaner.feature.pomodoro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle

/**
 * Skeleton of the Pomodoro screen
 */
@Composable
fun PomodoroScreen(
  onOpenHome: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenBookshelf: () -> Unit
) {
  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.home),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Home",
            onClick = onOpenHome
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.book),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Bücherregal",
            onClick = onOpenBookshelf
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.gear),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Settings",
            onClick = onOpenSettings
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.timer),
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Pomodoro",
            onClick = {}
          )
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      TopScreenTitle(title = "Pomodoro")
    }
  }
}