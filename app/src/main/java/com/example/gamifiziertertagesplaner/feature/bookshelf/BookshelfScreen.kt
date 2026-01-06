package com.example.gamifiziertertagesplaner.feature.bookshelf

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel

@Composable
fun BookshelfScreen(
  authViewModel: AuthViewModel,
  onOpenHome: () -> Unit,
  onOpenAchievements: () -> Unit,
  onOpenShop: () -> Unit,
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
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Settings",
            onClick = {}
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.shopping_cart),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Pomodoro",
            onClick = onOpenShop
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.trophy),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Bücherregal",
            onClick = onOpenAchievements
          )
        )
      )
    }
  ) { innerPadding ->
    val profilePoints = authViewModel.userProfile?.userPoints ?: 0
    val boughtBooks = authViewModel.userProfile?.boughtBooks ?: 0
    val boughtDecoration = authViewModel.userProfile?.boughtDecoration ?: 0
    val boughtPlants = authViewModel.userProfile?.boughtPlants ?: 0

    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        TopScreenTitle(title = "Bookshelf")

        // Bookshelf layout containing the items
        Bookshelf(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          boughtBooks = boughtBooks,
          boughtDecoration = boughtDecoration,
          boughtPlants = boughtPlants
        )

        // User score at the bottom of the page
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "Punktestand",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
          )

          Text(
            text = profilePoints.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
          )
        }
      }
    }
  }
}
