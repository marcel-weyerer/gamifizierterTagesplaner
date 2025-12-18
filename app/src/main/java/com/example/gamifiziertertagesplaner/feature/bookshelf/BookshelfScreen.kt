package com.example.gamifiziertertagesplaner.feature.bookshelf

import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import kotlinx.coroutines.delay

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

        // Punktestand
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

/**
 * Bookshelf layout containing the items.
 */
@Composable
private fun Bookshelf(
  modifier: Modifier,
  boughtBooks: Int,
  boughtDecoration: Int,
  boughtPlants: Int
) {
  // Clamp values to valid ranges to prevent IndexOutOfBounds
  val clampedBookCount = boughtBooks.coerceIn(0, bookSlots.size)
  val clampedDecorationCount = boughtDecoration.coerceIn(0, decorationSlots.size)
  val clampedPlantCount = boughtPlants.coerceIn(0, plantSlots.size)

  // Bookshelf layout values
  val shelfGap = 100.dp
  val shelfThickness = 24.dp
  val shelfColor = MaterialTheme.colorScheme.primary
  val totalBookshelfHeight = shelfGap * 3 + shelfThickness * 3

  // States to keep track of visible items
  var visibleBookCount by remember { mutableIntStateOf(0) }
  var visibleDecorationCount by remember { mutableIntStateOf(0) }
  var visiblePlantCount by remember { mutableIntStateOf(0) }

  // Reveal books one-by-one
  LaunchedEffect(clampedBookCount) {
    visibleBookCount = 0
    repeat(clampedBookCount) {
      delay(100)
      visibleBookCount++
    }
  }

  // Reveal decoration one-by-one
  LaunchedEffect(clampedDecorationCount) {
    visibleDecorationCount = 0
    repeat(clampedDecorationCount) {
      delay(100)
      visibleDecorationCount++
    }
  }

  // Reveal plant one-by-one
  LaunchedEffect(clampedPlantCount) {
    visiblePlantCount = 0
    repeat(clampedPlantCount) {
      delay(100)
      visiblePlantCount++
    }
  }

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
            // Gap above shelf for items
            Spacer(
              modifier = Modifier
                .fillMaxWidth()
                .height(shelfGap)
            )
            // Shelf
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
          // Books
          for (index in 0 until visibleBookCount) {
            val item = bookSlots[index]
            ItemAtSlot(item = item)
          }

          // Decoration
          for (index in 0 until visibleDecorationCount) {
            val item = decorationSlots[index]
            ItemAtSlot(item = item)
          }

          // Plant
          for (index in 0 until visiblePlantCount) {
            val item = plantSlots[index]
            ItemAtSlot(item = item)
          }
        }
      }
    }
  }
}

/**
 * Displays an item at a specific slot.
 */
@Composable
private fun ItemAtSlot(item: ItemSlot) {
  val shelfGap = 100.dp
  val shelfThickness = 24.dp
  val yOffset = item.row * (shelfGap + shelfThickness) + item.offsetY

  Box(
    modifier = Modifier
      .offset(x = item.offsetX, y = yOffset)
      .size(item.width, item.height)
      .graphicsLayer {
        rotationZ = item.rotation
      },
    contentAlignment = Alignment.BottomCenter
  ) {
    Image(
      painter = painterResource(id = item.resId),
      modifier = Modifier.fillMaxHeight(),
      contentDescription = "Book",
      contentScale = ContentScale.Fit
    )
  }
}
