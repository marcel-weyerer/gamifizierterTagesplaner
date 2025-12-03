package com.example.gamifiziertertagesplaner

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.*
import com.example.gamifiziertertagesplaner.feature.achievements.AchievementsScreen
import com.example.gamifiziertertagesplaner.feature.bookshelf.BookshelfScreen
import com.example.gamifiziertertagesplaner.feature.createTask.CreateTaskScreen
import com.example.gamifiziertertagesplaner.feature.home.MainScreen
import com.example.gamifiziertertagesplaner.feature.shop.ShopScreen
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.navigation.Routes

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
  // Setup navigation to all composable screens
  val navController = rememberNavController()

  // Task currently being edited (null = create mode)
  var taskToEdit by remember { mutableStateOf<Task?>(null) }

  NavHost(
    navController = navController,
    startDestination = Routes.HOME
  ) {
    composable(Routes.HOME) {         // Home Screen
      MainScreen(
        onOpenCreateTask = { navController.navigate(Routes.CREATE_TASK) },
        onOpenEditTask = { task ->
          taskToEdit = task              // store the task to edit
          navController.navigate(Routes.EDIT_TASK)
        },
        onOpenHome = { navController.navigate(Routes.HOME) },
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) }
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
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) },
        onOpenAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
        onOpenShop = { navController.navigate(Routes.SHOP) }
      )
    }

    composable(Routes.SHOP) {    // Shop Screen
      ShopScreen(
        onOpenBookshelf = { navController.navigate(Routes.BOOKSHELF) },
        onOpenAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
        onOpenShop = { navController.navigate(Routes.SHOP) }
      )
    }
  }
}