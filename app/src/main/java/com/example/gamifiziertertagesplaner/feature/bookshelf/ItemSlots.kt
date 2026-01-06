package com.example.gamifiziertertagesplaner.feature.bookshelf

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R

/**
 * Represents a slot on the bookshelf where an item can be placed.
 * It contains the item's offset in the slot, its size, and its rotation.
 */
data class ItemSlot(
  @DrawableRes val resId: Int,
  val row: Int,
  val offsetX: Dp,
  val offsetY: Dp = 0.dp,
  val width: Dp,
  val height: Dp,
  val rotation: Float = 0f,          // for leaning items
)

// Books
val bookSlots: List<ItemSlot> = listOf(
  ItemSlot(
    resId = R.drawable.book_1,
    row = 1,
    offsetX = 20.dp,
    width = 35.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_3,
    row = 0,
    offsetX = 100.dp,
    offsetY = 5.dp,
    width = 40.dp,
    height = 150.dp,
    rotation = 90f
  ),

  ItemSlot(
    resId = R.drawable.book_2,
    row = 2,
    offsetX = 230.dp,
    width = 25.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_3,
    row = 1,
    offsetX = 270.dp,
    offsetY = 5.dp,
    width = 40.dp,
    height = 150.dp,
    rotation = -90f
  ),

  ItemSlot(
    resId = R.drawable.book_5,
    row = 0,
    offsetX = 320.dp,
    width = 45.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_6,
    row = 2,
    offsetX = 0.dp,
    width = 50.dp,
    height = 100.dp,
  ),

  ItemSlot(
    resId = R.drawable.book_4,
    row = 1,
    offsetX = 135.dp,
    width = 35.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_5,
    row = 2,
    offsetX = 285.dp,
    width = 45.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_2,
    row = 0,
    offsetX = 10.dp,
    width = 25.dp,
    height = 100.dp,
    rotation = -10f
  ),

  ItemSlot(
    resId = R.drawable.book_2,
    row = 1,
    offsetX = 55.dp,
    width = 25.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_3,
    row = 2,
    offsetX = 255.dp,
    width = 40.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_6,
    row = 1,
    offsetX = 290.dp,
    offsetY = (-25).dp,
    width = 50.dp,
    height = 140.dp,
    rotation = 90f
  ),

  ItemSlot(
    resId = R.drawable.book_1,
    row = 0,
    offsetX = 285.dp,
    width = 35.dp,
    height = 100.dp,
    rotation = 20f
  ),

  ItemSlot(
    resId = R.drawable.book_4,
    row = 2,
    offsetX = 30.dp,
    width = 35.dp,
    height = 100.dp,
    rotation = -10f
  ),

  ItemSlot(
    resId = R.drawable.book_1,
    row = 2,
    offsetX = 200.dp,
    width = 35.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_3,
    row = 1,
    offsetX = 75.dp,
    width = 40.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_4,
    row = 0,
    offsetX = 100.dp,
    offsetY = (-18).dp,
    width = 35.dp,
    height = 140.dp,
    rotation = 90f
  ),

  ItemSlot(
    resId = R.drawable.book_5,
    row = 1,
    offsetX = 105.dp,
    width = 45.dp,
    height = 100.dp
  ),

  ItemSlot(
    resId = R.drawable.book_2,
    row = 0,
    offsetX = 100.dp,
    offsetY = (-15).dp,
    width = 25.dp,
    height = 100.dp,
    rotation = -90f
  ),

  ItemSlot(
    resId = R.drawable.book_6,
    row = 1,
    offsetX = 150.dp,
    width = 50.dp,
    height = 100.dp,
    rotation = -10f
  ),

  ItemSlot(
    resId = R.drawable.book_4,
    row = 2,
    offsetX = 325.dp,
    width = 35.dp,
    height = 100.dp
  ),
)

// Decoration
val decorationSlots: List<ItemSlot> = listOf(
  ItemSlot(
    resId = R.drawable.decoration_1,
    row = 2,
    offsetX = 75.dp,
    offsetY = 3.dp,
    width = 100.dp,
    height = 100.dp
  ),
)

// Plants
val plantSlots: List<ItemSlot> = listOf(
  ItemSlot(
    resId = R.drawable.plant_1,
    row = 0,
    offsetX = 180.dp,
    width = 100.dp,
    height = 100.dp
  ),
)