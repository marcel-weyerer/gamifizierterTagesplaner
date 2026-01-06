package com.example.gamifiziertertagesplaner.feature.bookshelf

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import kotlinx.coroutines.delay

/**
 * Bookshelf layout containing the items
 *
 * @param boughtBooks         The number of bought books.
 * @param boughtDecoration    The number of bought decoration.
 * @param boughtPlants        The number of bought plants.
 */
@Composable
fun Bookshelf(
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
 * Composable that places an item at a specific slot.
 *
 * @param item  The item to place.
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
      contentDescription = "Item",
      contentScale = ContentScale.Fit
    )
  }
}