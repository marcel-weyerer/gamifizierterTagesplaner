package com.example.gamifiziertertagesplaner

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.gamifiziertertagesplaner.feature.createTask.CreateTaskScreen
import com.example.gamifiziertertagesplaner.feature.home.MainScreen
import com.example.gamifiziertertagesplaner.navigation.Routes

@Composable
fun App() {
  // Setup navigation to all composable screens
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = Routes.HOME
  ) {
    composable(Routes.HOME) {         // Home Screen
      MainScreen(
        onOpenCreateTask = { navController.navigate(Routes.CREATE_TASK) }
      )
    }
    composable(Routes.CREATE_TASK) {  // Create Task Screen
      CreateTaskScreen(
        onBack = { navController.navigateUp() }
      )
    }
  }
}