package com.example.gamifiziertertagesplaner.feature.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
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

        Bookshelf(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          boughtBooks = boughtBooks
        )

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Punktestand
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

@Composable
fun Bookshelf(
  modifier: Modifier,
  boughtBooks: Int
) {
  val clampedCount = boughtBooks.coerceIn(0, bookSlots.size)
  val shelfGap = 100.dp
  val shelfThickness = 24.dp
  val shelfColor = MaterialTheme.colorScheme.primary
  val totalBookshelfHeight = shelfGap * 3 + shelfThickness * 3

  Column(
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .height(totalBookshelfHeight)
          .fillMaxWidth()
      ) {
        // Draw shelves as background
        Column(
          modifier = Modifier.matchParentSize()
        ) {
          repeat(3) {
            // gap above shelf
            Spacer(
              modifier = Modifier
                .fillMaxWidth()
                .height(shelfGap)
            )
            // shelf itself
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(shelfThickness)
                .background(shelfColor)
            )
          }
        }

        // Overlay books at fixed positions
        Box(
          modifier = Modifier.matchParentSize()
        ) {
          for (index in 0 until clampedCount) {
            val slot = bookSlots[index]
            BookAtSlot(slot = slot)
          }
        }
      }
    }
  }
}

@Composable
private fun BookAtSlot(slot: BookSlot) {
  val shelfGap = 100.dp
  val shelfThickness = 24.dp
  val yOffset = slot.row * (shelfGap + shelfThickness) + slot.offsetY

  Box(
    modifier = Modifier
      .offset(x = slot.offsetX, y = yOffset)
      .size(slot.width, slot.height)
      .graphicsLayer {
        rotationZ = slot.rotation
      },
    contentAlignment = Alignment.BottomCenter
  ) {
    Icon(
      painter = painterResource(id = slot.resId),
      modifier = Modifier.fillMaxHeight(),
      contentDescription = "Book",
      tint = Color.Unspecified
    )
  }
}
