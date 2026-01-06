package com.example.gamifiziertertagesplaner.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.ui.theme.Ivory
import com.example.gamifiziertertagesplaner.ui.theme.PriorityOrange
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.example.gamifiziertertagesplaner.ui.theme.PriorityYellow
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.focusedShadowElevation
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable for a single task. It contains all information about the task and options to delete
 * or edit the task.
 *
 * @param viewModel       ViewModel for the Home screen.
 * @param task            Task to be displayed.
 * @param onOpenEditTask  Callback invoked when an edit button is pressed.
 */
@Composable
fun TaskView(
  viewModel: HomeViewModel,
  task: Task,
  onOpenEditTask: (Task) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  // Completion effect states
  var isCompleting by remember { mutableStateOf(false) }
  var measuredHeightPx by remember { mutableIntStateOf(0) }
  val density = LocalDensity.current
  val scope = rememberCoroutineScope()

  val targetHeightDp = with(density) { measuredHeightPx.toDp() }

  // Make height grow from 0 to targetHeight
  val overlayHeight by animateDpAsState(
    targetValue = if (isCompleting) targetHeightDp else 0.dp,
    animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing),
    label = "completeOverlayHeight"
  )

  val shape = RoundedCornerShape(cornerRadius)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .onSizeChanged { measuredHeightPx = it.height }   // Measure actual rendered height
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = if (task.state == 2) Ivory else MaterialTheme.colorScheme.secondary,
      shape = shape,
      shadowElevation = if (task.state == 2) focusedShadowElevation else shadowElevation,
      onClick = { isExpanded = !isExpanded }
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .animateContentSize()
      ) {
        // Minimalistic view
        MinimalInformation(
          task = task,
          viewModel = viewModel,
          onShortPressCheck = {
            if (isCompleting) return@MinimalInformation

            // Only do the effect when state is not 0
            if (task.state != 0) {
              isExpanded = false        // Hide secondary information
              isCompleting = true

              scope.launch {
                delay(500)    // Keep overlay visible
                isCompleting = false    // Remove overlay
                viewModel.toggleTaskStatus(task, isLongPress = false)     // Set task to done
              }
            } else {
              // Change status if effect is done
              viewModel.toggleTaskStatus(task, isLongPress = false)
            }
          },
          onLongPressCheck = {
            if (!isCompleting) {
              viewModel.toggleTaskStatus(task, isLongPress = true)
            }
          },
          isExpanded = isExpanded
        )

        // Secondary information
        if (isExpanded && !isCompleting) {
          SecondaryInformation(
            task = task,
            onOpenEditTask = onOpenEditTask,
            onShowDeleteDialog = { showDeleteDialog = true }
          )
        }
      }
      if (showDeleteDialog) {
        CustomAlertDialog(
          task = task,
          viewModel = viewModel,
          onHideDeleteDialog = { showDeleteDialog = false }
        )
      }
    }

    // Overlay task complete effect
    if (isCompleting) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(overlayHeight)
          .align(Alignment.Center)
          .clip(shape)
          .zIndex(1f),
        shape = shape,
        color = Ivory,
        shadowElevation = 0.dp
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          // Show received points
          Text(
            text = "+${task.points} Punkte",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surfaceVariant
          )
        }
      }
    }
  }
}

/**
 * Minimalistic view of a task.
 * It contains the checkbox, time information, title and priority indicator
 *
 * @param task    Task to be displayed.
 * @param viewModel   ViewModel for the Home screen.
 */
@Composable
private fun MinimalInformation(
  task: Task,
  viewModel: HomeViewModel,
  onShortPressCheck: () -> Unit,
  onLongPressCheck: () -> Unit,
  isExpanded: Boolean
) {
  val haptic = LocalHapticFeedback.current
  val density = LocalDensity.current

  // Setup text visual style depending on status
  val textColor = if (task.state == 0) {
    MaterialTheme.colorScheme.surfaceVariant
  } else {
    MaterialTheme.colorScheme.onSecondary
  }

  val titleDecoration = if (task.state == 0) {
    TextDecoration.LineThrough
  } else {
    TextDecoration.None
  }

  val iconBoxSize = with(LocalDensity.current) { (50.dp).roundToPx() }
  var rowHeight by remember { mutableIntStateOf(0) }

  // Offset icon to the center of the row
  val targetCenterOffset = remember(rowHeight, iconBoxSize) {
    with(density) {
      (((rowHeight - iconBoxSize) / 2f).coerceAtLeast(0f)).toDp()
    }
  }

  // Animate whenever the row height changes
  val iconOffsetY by animateDpAsState(
    targetValue = targetCenterOffset,
    animationSpec = tween(durationMillis = 220, easing = LinearOutSlowInEasing),
    label = "iconCenterOffset"
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp, horizontal = 10.dp)
      .onSizeChanged { rowHeight = it.height },
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Check icon button
    Box(
      modifier = Modifier
        .size(50.dp)
        .offset(y = iconOffsetY)
        .align(Alignment.Top)
        .combinedClickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            onShortPressCheck()
          },
          onLongClick = {
            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
            onLongPressCheck()
          }
        )
    ) {
      // Choose check circle depending on state
      val painterRes = when (task.state) {
        1 -> painterResource(R.drawable.check_state_1)
        2 -> painterResource(R.drawable.check_state_2)
        3 -> painterResource(R.drawable.check_state_1)
        else -> painterResource(R.drawable.check_state_0)
      }

      Icon(
        modifier = Modifier.size(50.dp),
        painter = painterRes,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSecondary
      )
    }

    // Title and time information
    Column(
      modifier = Modifier
        .padding(horizontal = 5.dp)
        .weight(1f)
    ) {
      val timeString = remember(task.startTime, task.duration) {
        buildTimeString(task)
      }

      // Time information
      if (!timeString.isEmpty()) {
        Text(
          text = timeString,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))
      }

      // Title
      if (isExpanded) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge,
          color = textColor,
          textDecoration = titleDecoration
        )
      } else {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge,
          color = textColor,
          textDecoration = titleDecoration,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    Box(
      modifier = Modifier
        .align(Alignment.Top)
        .width(70.dp)
        .padding(end = 10.dp),
      contentAlignment = Alignment.TopEnd
    ) {
      // Priority indicator
      val priorityColor = when (task.priority) {
        1 -> PriorityRed
        2 -> PriorityOrange
        3 -> PriorityYellow
        else -> Color.Transparent
      }

      // Choose icon depending on state
      val painterRes = when (task.state) {
        2 -> painterResource(R.drawable.bookmark_long)
        else -> painterResource(R.drawable.bookmark_short)
      }

      Icon(
        modifier = Modifier
          .size(60.dp)
          .offset(y = (-5).dp),
        painter = painterRes,
        contentDescription = "Priorität",
        tint = priorityColor,
      )

      if (task.state == 3) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
          modifier = Modifier.align(Alignment.BottomCenter),
          text = "Pausiert",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.surfaceVariant,
        )
      }
    }
  }
}

/**
 * Secondary information of a task.
 * It contains the task description and reminder information and action buttons.
 *
 * @param task                Task to be displayed.
 * @param onOpenEditTask      Callback invoked when an edit button is pressed.
 * @param onShowDeleteDialog  Callback invoked when the delete button is pressed.
 */
@Composable
private fun SecondaryInformation(task: Task, onOpenEditTask: (Task) -> Unit, onShowDeleteDialog: () -> Unit) {
  val textColor = if (task.state == 0) {
    MaterialTheme.colorScheme.surfaceVariant
  } else {
    MaterialTheme.colorScheme.onSecondary
  }

  HorizontalDivider(
    modifier = Modifier
      .fillMaxWidth()
      .padding(
        horizontal = 24.dp,
        vertical = 10.dp
      ),
    thickness = 2.dp,
    color = MaterialTheme.colorScheme.surfaceVariant
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(
        horizontal = 48.dp,
        vertical = 10.dp
      )
  ) {
    // Task description
    task.description?.let { description ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(IntrinsicSize.Min)
      ) {
        Box(
          modifier = Modifier
            .fillMaxHeight()
            .width(2.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Text(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp),
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          color = textColor
        )
      }
    }

    // Reminder information
    task.reminder?.let { minutes ->
      Spacer(Modifier.height(20.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
      ) {
        // Reminder icon
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = null,
          tint = textColor,
          modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(10.dp))

        // Reminder value
        Text(
          text = "$minutes min vorher",
          style = MaterialTheme.typography.bodyMedium,
          color = textColor
        )
      }
    }

    Spacer(Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      IconButton(
        onClick = { onOpenEditTask(task) }
      ) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Edit",
          tint = MaterialTheme.colorScheme.onSecondary
        )
      }
      IconButton(
        onClick = onShowDeleteDialog,
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Delete",
          tint = PriorityRed
        )
      }
    }
  }
}

/**
 * Custom alert dialog for deleting a task.
 *
 * @param task            Task to be deleted.
 * @param viewModel       ViewModel for the Home screen.
 * @param onHideDeleteDialog  Callback invoked when the delete button is pressed.
 */
@Composable
private fun CustomAlertDialog(task: Task, viewModel: HomeViewModel, onHideDeleteDialog: () -> Unit) {
  AlertDialog(
    onDismissRequest = onHideDeleteDialog,
    title = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Task sicher löschen?",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onPrimary,
          textAlign = TextAlign.Center
        )
      }
    },
    confirmButton = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Button(
          onClick = onHideDeleteDialog,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
          )
        ) {
          Text(
            text = "Abbrechen",
            style = MaterialTheme.typography.bodySmall
          )
        }

        Spacer(Modifier.width(16.dp))

        Button(
          onClick = {
            viewModel.deleteTask(task.id)
            onHideDeleteDialog
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = PriorityRed,
            contentColor = Color.White
          )
        ) {
          Text(
            text = "Löschen",
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    containerColor = MaterialTheme.colorScheme.primary
  )
}

/**
 * Builds a time string for a task depending on the presence of start time and or duration
 */
private fun buildTimeString(task: Task): String {
  val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  var timeString = ""

  if (task.startTime != null) {
    timeString = dateFormat.format(task.startTime.toDate())

    if (task.duration != null) {
      val endTime = dateFormat.format(
        Date((task.startTime.toDate().time) + (task.duration * 60000L))
      )

      timeString += " - $endTime"
    }
  } else if (task.duration != null) {
    timeString = String.format(
      Locale.getDefault(),
      "%02dh %02dmin",
      task.duration / 60,
      task.duration % 60
    )
  }

  return timeString
}