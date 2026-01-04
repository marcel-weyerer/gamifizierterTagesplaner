package com.example.gamifiziertertagesplaner.feature.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.firestore.Task

@Composable
fun MainScreen(
  homeViewModel: HomeViewModel,
  onOpenHome: () -> Unit,
  onOpenCreateTask: () -> Unit,
  onOpenEditTask: (Task) -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenPomodoro: () -> Unit
) {
  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.home),
            tint = MaterialTheme.colorScheme.surface,
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
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Pomodoro",
            onClick = onOpenPomodoro
          )
        )
      )
    }
  ) { innerPadding ->
    HomeScreenContent(innerPadding, homeViewModel, onOpenCreateTask, onOpenEditTask, onOpenBookshelf, onOpenSettings)
  }
}

/**
 * Main screen content
 */
@Composable
private fun HomeScreenContent(
  innerPadding: PaddingValues,
  viewModel: HomeViewModel,
  onOpenCreateTask: () -> Unit,
  onOpenEditTask: (Task) -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenSettings: () -> Unit
) {
  val tasks by viewModel.tasks.collectAsState()     // List of all tasks
  val hasTasks = tasks.isNotEmpty()     // Flag if there are tasks
  val circleOffsetY by animateDpAsState(
    targetValue = if (hasTasks) (-175).dp else 0.dp,
    animationSpec = tween(600),
    label = "circleOffset"
  )

  TopAppBar(circleOffsetY, innerPadding, viewModel, onOpenCreateTask, onOpenBookshelf, onOpenSettings)

  TaskList(circleOffsetY, innerPadding, viewModel, onOpenEditTask)
}