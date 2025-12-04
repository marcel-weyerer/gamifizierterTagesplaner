package com.example.gamifiziertertagesplaner.feature.pomodoro

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle

@Composable
fun PomodoroScreen(
  onOpenHome: () -> Unit
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
            icon = painterResource(R.drawable.timer),
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Shop",
            onClick = {}
          ),
        )
      )
    }
  ) { innerPadding ->
    TopScreenTitle(
      innerPadding = innerPadding,
      title = "Pomodoro"
    )
  }
}