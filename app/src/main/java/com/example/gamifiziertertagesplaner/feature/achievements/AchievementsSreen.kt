package com.example.gamifiziertertagesplaner.feature.achievements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar

@Composable
fun AchievementsScreen(
  onOpenBookshelf: () -> Unit,
  onOpenShop: () -> Unit
) {
  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.trophy),
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Achievements",
            onClick = {}
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.book),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Bookshelf",
            onClick = onOpenBookshelf
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
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = "Achievements",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}