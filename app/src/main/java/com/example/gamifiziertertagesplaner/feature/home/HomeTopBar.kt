package com.example.gamifiziertertagesplaner.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Custom top app bar containing the date and navigation buttons.
 */
@Composable
fun TopAppBar(
  circleOffsetY: Dp,
  innerPadding: PaddingValues,
  viewModel: HomeViewModel,
  onOpenCreateTask: () -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenSettings: () -> Unit
) {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .padding(innerPadding)
      .offset(y = circleOffsetY)
  ) {
    // Button offsets
    val offsetX = maxWidth / 3f
    val offsetY = (-20).dp

    val halfHeight = maxHeight / 2f

    // Circle background
    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            color = MediumBrown,
            radius = halfHeight.toPx(),
            center = Offset(x = size.width / 2f, y = 0f)
          )
        }
    ) {
      // Date
      DateDisplay(
        topPadding = halfHeight / 4f,
        viewModel = viewModel
      )

      // Add button
      TopNavigationButton(
        onOpenPage = onOpenCreateTask,
        modifier = Modifier.align(Alignment.Center),
        buttonSize = 100.dp,
        offsetX = 0.dp,
        offsetY = 0.dp,
        icon = painterResource(R.drawable.plus),
        iconSize = 48.dp
      )

      // Settings
      TopNavigationButton(
        onOpenPage = onOpenSettings,
        modifier = Modifier.align(Alignment.Center),
        buttonSize = 50.dp,
        offsetX = -offsetX,
        offsetY = offsetY,
        icon = painterResource(R.drawable.gear),
        iconSize = 30.dp
      )

      // Bookshelf
      TopNavigationButton(
        onOpenPage = onOpenBookshelf,
        modifier = Modifier.align(Alignment.Center),
        buttonSize = 50.dp,
        offsetX = offsetX,
        offsetY = offsetY,
        icon = painterResource(R.drawable.book),
        iconSize = 30.dp
      )
    }
  }
}

/**
 * Custom composable for the date display.
 *
 * @param topPadding  Padding at the top of the screen.
 * @param viewModel   ViewModel for the Home screen.
 */
@Composable
private fun DateDisplay(topPadding: Dp, viewModel: HomeViewModel) {
  val tasks by viewModel.tasks.collectAsState()     // List of all tasks
  val hasTasks = tasks.isNotEmpty()     // Flag if there are tasks

  val dateOffsetY = if (hasTasks) 110.dp else (-128).dp

  val yearText = viewModel.yearText
  val dayText = viewModel.dayText

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = topPadding),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .animateContentSize(),
        contentAlignment = Alignment.Center
      ) {
        if (!hasTasks) {
          Text(
            text = yearText,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.surface
          )
        }
      }

      Text(
        modifier = Modifier.offset(y = dateOffsetY),
        text = dayText,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onPrimary
      )
    }
  }
}

/**
 * Custom composable for the top navigation buttons.
 *
 * @param onOpenPage          Callback invoked when the button is pressed.
 * @param modifier            Modifier for the button.
 * @param buttonSize          Size of the button.
 * @param offsetX             Horizontal offset of the button.
 * @param offsetY             Vertical offset of the button.
 * @param icon                Icon to be displayed on the button.
 * @param iconSize            Size of the icon.
 */
@Composable
private fun TopNavigationButton(
  onOpenPage: () -> Unit,
  modifier: Modifier = Modifier,
  buttonSize: Dp,
  offsetX: Dp,
  offsetY: Dp,
  icon: Painter,
  iconSize: Dp
) {
  ElevatedButton(
    onClick = onOpenPage,
    modifier = modifier
      .size(buttonSize)
      .offset(x = offsetX, y = offsetY),
    contentPadding = PaddingValues(0.dp),
    colors = ButtonDefaults.elevatedButtonColors(
      containerColor = MaterialTheme.colorScheme.secondary,
      contentColor = MaterialTheme.colorScheme.onSecondary
    ),
    elevation = ButtonDefaults.elevatedButtonElevation(
      defaultElevation = shadowElevation,
      pressedElevation = 0.dp
    )
  ) {
    Icon(
      modifier = Modifier.size(iconSize),
      painter = icon,
      contentDescription = null
    )
  }
}