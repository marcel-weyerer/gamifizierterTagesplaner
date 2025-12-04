package com.example.gamifiziertertagesplaner.feature.bookshelf

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle

@Composable
fun BookshelfScreen(
  onOpenHome: () -> Unit,
  onOpenAchievements: () -> Unit,
  onOpenShop: () -> Unit,
) {
  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.trophy),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Achievements",
            onClick = onOpenAchievements
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.home),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Home",
            onClick = onOpenHome
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.shopping_cart),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Shop",
            onClick = onOpenShop
          ),
        )
      )
    }
  ) { innerPadding ->
    TopScreenTitle(
      innerPadding = innerPadding,
      title = "Bücherregal"
    )
  }
}