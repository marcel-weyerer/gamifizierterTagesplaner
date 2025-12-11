package com.example.gamifiziertertagesplaner

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.gamifiziertertagesplaner.feature.achievements.AchievementsScreen
import com.example.gamifiziertertagesplaner.feature.bookshelf.BookshelfScreen
import com.example.gamifiziertertagesplaner.feature.createTask.CreateTaskScreen
import com.example.gamifiziertertagesplaner.feature.home.MainScreen
import com.example.gamifiziertertagesplaner.feature.login.LoginScreen
import com.example.gamifiziertertagesplaner.feature.login.SignUpScreen
import com.example.gamifiziertertagesplaner.feature.pomodoro.PomodoroScreen
import com.example.gamifiziertertagesplaner.feature.settings.SettingsScreen
import com.example.gamifiziertertagesplaner.feature.shop.ShopScreen
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.navigation.Routes

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
  // Setup navigation to all composable screens
  val navController = rememberNavController()
  val authViewModel: AuthViewModel = viewModel()

  // Task currently being edited (null = create mode)
  var taskToEdit by remember { mutableStateOf<Task?>(null) }

  LaunchedEffect(Unit) {
    if (authViewModel.repositoryCurrentUser() != null) {
      navController.navigate(Routes.HOME) {
        popUpTo(0) { inclusive = true }
      }
    } else {
      navController.navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
      }
    }
  }

  NavHost(
    navController = navController,
    startDestination = Routes.LOGIN
  ) {
    composable(Routes.LOGIN) {        // Login Screen
      LoginScreen(
        authViewModel = authViewModel,
        onLoggedIn = {
          navController.navigate(Routes.HOME) {
            popUpTo(Routes.LOGIN) { inclusive = true }
          }
        },
        onSignUp = { navController.navigate(Routes.SIGNUP) }
      )
    }

    composable("signup") {
      SignUpScreen(
        authViewModel = authViewModel,
        onSignedUp = {
          navController.navigate(Routes.HOME) {
            popUpTo(Routes.LOGIN) { inclusive = true }
          }
        },
        onBackToLogin = {
          navController.popBackStack()
        }
      )
    }

    composable(Routes.HOME) {         // Home Screen
      MainScreen(
        onOpenCreateTask = { navController.navigate(Routes.CREATE_TASK) },
        onOpenEditTask = { task ->
          taskToEdit = task              // store the task to edit
          navController.navigate(Routes.EDIT_TASK)
        },
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) },
        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
        onOpenPomodoro = { navController.navigate(Routes.POMODORO) }
      )
    }

    composable(Routes.CREATE_TASK) {  // Create Task Screen
      CreateTaskScreen(
        onCancel = {
          navController.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = true }
          }
        }
      )
    }

    composable(Routes.EDIT_TASK) {    // Edit Task Screen
      CreateTaskScreen(
        taskToEdit = taskToEdit,
        onCancel = {
          navController.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = true }
          }
        }
      )
    }

    composable(Routes.BOOKSHELF) {    // Bookshelf Screen
      BookshelfScreen(
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
        onOpenShop = { navController.navigate(Routes.SHOP) }
      )
    }

    composable(Routes.ACHIEVEMENTS) {    // Achievements Screen
      AchievementsScreen(
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) },
        onOpenShop = { navController.navigate(Routes.SHOP) }
      )
    }

    composable(Routes.SHOP) {    // Shop Screen
      ShopScreen(
        authViewModel = authViewModel,
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) },
        onOpenAchievements = { navController.navigate(Routes.ACHIEVEMENTS) }
      )
    }

    composable(Routes.SETTINGS) {    // Settings Screen
      SettingsScreen(
        authViewModel = authViewModel,
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenPomodoro = { navController.navigate(Routes.POMODORO) },
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) },
        onLoggedOut = { navController.navigate(Routes.LOGIN) }
      )
    }

    composable(Routes.POMODORO) {    // Pomodoro Screen
      PomodoroScreen(
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) }
      )
    }
  }
}