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
import com.example.gamifiziertertagesplaner.feature.LoadingScreen
import com.example.gamifiziertertagesplaner.feature.achievements.AchievementsScreen
import com.example.gamifiziertertagesplaner.feature.bookshelf.BookshelfScreen
import com.example.gamifiziertertagesplaner.feature.createTask.CreateTaskScreen
import com.example.gamifiziertertagesplaner.feature.endOfDay.EndOfDayScreen
import com.example.gamifiziertertagesplaner.feature.home.HomeViewModel
import com.example.gamifiziertertagesplaner.feature.home.MainScreen
import com.example.gamifiziertertagesplaner.feature.login.LoginScreen
import com.example.gamifiziertertagesplaner.feature.login.SignUpScreen
import com.example.gamifiziertertagesplaner.feature.pomodoro.PomodoroScreen
import com.example.gamifiziertertagesplaner.feature.settings.SettingsScreen
import com.example.gamifiziertertagesplaner.feature.shop.ShopScreen
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.navigation.Routes
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
  // Setup navigation to all composable screens
  val navController = rememberNavController()
  val authViewModel: AuthViewModel = viewModel()
  val homeViewModel: HomeViewModel = viewModel()

  val profile = authViewModel.userProfile

  // Task currently being edited (null = create mode)
  var taskToEdit by remember { mutableStateOf<Task?>(null) }

  var initialNavigationDone by remember { mutableStateOf(false) }

  LaunchedEffect(authViewModel.repositoryCurrentUser(), profile) {
    if (initialNavigationDone) return@LaunchedEffect

    val currentUser = authViewModel.repositoryCurrentUser()

    when {
      currentUser == null -> {
        navController.navigate(Routes.LOGIN) {
          popUpTo(Routes.LOADING) { inclusive = true }
        }
        initialNavigationDone = true
      }

      profile == null -> {
        // Keep showing LoadingScreen when user is logged in, but profile not loaded yet
      }

      else -> {
        val target = if (hasDayEnded(profile.endOfDayTime)) Routes.ENDOFDAY else Routes.HOME

        if (target == Routes.HOME) {
          homeViewModel.loadTasks()
        }

        navController.navigate(target) {
          popUpTo(Routes.LOADING) { inclusive = true }
        }
        initialNavigationDone = true
      }
    }
  }

  NavHost(
    navController = navController,
    startDestination = Routes.LOADING
  ) {
    composable (Routes.LOADING) {     // Loading Screen
      LoadingScreen()
    }

    composable(Routes.LOGIN) {        // Login Screen
      LoginScreen(
        authViewModel = authViewModel,
        onLoggedIn = {
          homeViewModel.loadTasks()
          navController.navigate(Routes.HOME) {
            popUpTo(Routes.LOGIN) { inclusive = true }
          }
        },
        onSignUp = { navController.navigate(Routes.SIGNUP) }
      )
    }

    composable(Routes.SIGNUP) {       // Signup Screen
      SignUpScreen(
        authViewModel = authViewModel,
        onSignedUp = {
          homeViewModel.loadTasks()
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
        homeViewModel = homeViewModel,
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
          homeViewModel.loadTasks()
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
          homeViewModel.loadTasks()
          navController.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = true }
          }
        }
      )
    }

    composable(Routes.BOOKSHELF) {    // Bookshelf Screen
      BookshelfScreen(
        authViewModel = authViewModel,
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

    composable(Routes.ENDOFDAY) {    // EndOfDay Screen
      EndOfDayScreen(
        homeViewModel = homeViewModel,
        authViewModel = authViewModel,
        onOpenHome = { navController.navigate(Routes.HOME) }
      )
    }
  }
}

@RequiresApi(Build.VERSION_CODES.O)
fun hasDayEnded(endOfDayTime: Timestamp?): Boolean {
  if (endOfDayTime == null) return false

  val endDateTime = endOfDayTime.toDate()
    .toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()

  val nowDateTime = LocalDateTime.now()

  return nowDateTime >= endDateTime
}