package com.example.gamifiziertertagesplaner.feature.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown
import com.example.gamifiziertertagesplaner.ui.theme.PriorityOrange
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.example.gamifiziertertagesplaner.ui.theme.PriorityYellow
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun MainScreen(
  viewModel: HomeViewModel = viewModel(),
  onOpenHome: () -> Unit,
  onOpenCreateTask: () -> Unit,
  onOpenEditTask: (Task) -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenPomodoro: () -> Unit
) {
  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.home),
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Home",
            onClick = onOpenHome
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.book),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Bücherregal",
            onClick = onOpenBookshelf
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.gear),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Settings",
            onClick = onOpenSettings
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.timer),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Pomodoro",
            onClick = onOpenPomodoro
          )
        )
      )
    }
  ) { innerPadding ->
    HomeScreenContent(innerPadding, viewModel, onOpenCreateTask, onOpenEditTask, onOpenBookshelf, onOpenSettings)
  }
}

/**
 * Main screen content
 */
@Composable
private fun HomeScreenContent(
  innerPadding: PaddingValues,
  viewModel: HomeViewModel,
  onOpenCreateTask: () -> Unit,
  onOpenEditTask: (Task) -> Unit,
  onOpenBookshelf: () -> Unit,
  onOpenSettings: () -> Unit
) {
  val tasks by viewModel.tasks.collectAsState()     // List of all tasks
  val hasTasks = tasks.isNotEmpty()     // Flag if there are tasks
  val circleOffsetY by animateDpAsState(
    targetValue = if (hasTasks) (-175).dp else 0.dp,
    animationSpec = tween(600),
    label = "circleOffset"
  )

  TopAppBar(circleOffsetY, innerPadding, viewModel, onOpenCreateTask, onOpenBookshelf, onOpenSettings)

  TaskList(circleOffsetY, innerPadding, viewModel, onOpenEditTask)
}

/**
 * Custom top app bar containing the date and navigation buttons.
 */
@Composable
private fun TopAppBar(
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

/**
 * Custom composable for the task list.
 *
 * @param circleOffsetY   Vertical offset of the circle.
 * @param innerPadding    Padding for the inner content.
 * @param viewModel       ViewModel for the Home screen.
 * @param onOpenEditTask  Callback invoked when an edit button is pressed.
 */
@Composable
private fun TaskList(
  circleOffsetY: Dp,
  innerPadding: PaddingValues,
  viewModel: HomeViewModel,
  onOpenEditTask: (Task) -> Unit
) {
  val tasks by viewModel.tasks.collectAsState()   // List of all tasks
  val hasTasks = tasks.isNotEmpty()

  val sortedTasks = remember(tasks) {
    tasks.sortedBy { it.priority }
  }

  val isLoading = viewModel.isLoading.collectAsState().value    // Currently fetching tasks

  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
  ) {
    val spacerHeight = maxHeight / 2 + circleOffsetY

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(spacerHeight))    // Start list below the topbar

      val totalPoints by viewModel.totalPoints.collectAsState()
      val receivedPoints by viewModel.receivedPoints.collectAsState()
      // Progress bar
      if (hasTasks) {
        TaskProgressBar(
          receivedPoints = receivedPoints,
          totalPoints = totalPoints
        )
      }

      Box(
        modifier = Modifier
          .fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        when {
          isLoading -> {    // Show loading indicator when currently fetching tasks
            CircularProgressIndicator(
              modifier = Modifier.size(48.dp),
              color = MaterialTheme.colorScheme.background,
              trackColor = MaterialTheme.colorScheme.primary
            )
          }

          !hasTasks -> {   // No task in database
            Text(
              text = "Erstelle einen\nTagesplan",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onBackground,
              textAlign = TextAlign.Center
            )
          }

          else -> {     // Tasks found
            // Split tasks into active and finished tasks
            val (doneTasks, activeTasks) = sortedTasks.partition { it.state == 0 }

            // Build priority groups only from active tasks
            val priorityGroups = listOf(1, 2, 3).mapNotNull { priority ->
              val groupTasks = activeTasks.filter { it.priority == priority }
              if (groupTasks.isEmpty()) null else priority to groupTasks
            }

            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .align(Alignment.TopCenter),
            ) {
              // Sections for active tasks depending on priority
              priorityGroups.forEach { (priority, groupTasks) ->
                // Header per priority group
                item {
                  SectionHeader(
                    text = when (priority) {
                      1 -> "Hohe Priorität"
                      2 -> "Mittlere Priorität"
                      3 -> "Niedrige Priorität"
                      else -> ""
                    }
                  )
                }

                // All tasks in this priority group
                items(
                  items = groupTasks,
                  key = { it.id }
                ) { task ->
                  TaskView(viewModel, task) { onOpenEditTask(task) }
                  Spacer(Modifier.height(10.dp))
                }
              }

              // Section for finished tasks
              if (doneTasks.isNotEmpty()) {
                item {
                  SectionHeader(text = "Erledigt")
                }

                items(
                  items = doneTasks,
                  key = { it.id }
                ) { task ->
                  TaskView(viewModel, task) { onOpenEditTask(task) }
                  Spacer(Modifier.height(10.dp))
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Custom composable for the progress bar.
 * It shows the progress of the finished task by using the reveived points and the total points.
 *
 * @param receivedPoints  Number of received points from finished tasks.
 * @param totalPoints     Total number of points from all tasks of the day.
 */
@Composable
private fun TaskProgressBar(
  receivedPoints: Int,
  totalPoints: Int
) {
  val targetProgress = if (totalPoints > 0) (receivedPoints.toFloat() / totalPoints) else 0f

  // Smooth animation between values
  val animatedProgress by animateFloatAsState(targetValue = targetProgress)

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 10.dp),
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation
  ) {
    LinearProgressIndicator(
      progress = { animatedProgress },
      modifier = Modifier
        .fillMaxWidth()
        .height(24.dp),
      color = MaterialTheme.colorScheme.onSecondary,
      trackColor = MaterialTheme.colorScheme.secondary
    )
  }
}


/**
 * Composable for a single task. It contains all information about the task and options to delete
 * or edit the task.
 *
 * @param viewModel       ViewModel for the Home screen.
 * @param task            Task to be displayed.
 * @param onOpenEditTask  Callback invoked when an edit button is pressed.
 */
@Composable
private fun TaskView(
  viewModel: HomeViewModel,
  task: Task,
  onOpenEditTask: (Task) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation,
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
        viewModel = viewModel
      )

      // Secondary information
      if (isExpanded) {
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
}

/**
 * Minimalistic view of a task.
 * It contains the checkbox, time information, title and priority indicator
 *
 * @param task    Task to be displayed.
 * @param viewModel   ViewModel for the Home screen.
 */
@Composable
private fun MinimalInformation(task: Task, viewModel: HomeViewModel) {
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

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Check icon button
    IconButton(
      modifier = Modifier.size(50.dp),
      onClick = { viewModel.toggleTaskStatus(task) }
    ) {
      var painterRes = painterResource(R.drawable.check_state_1)

      when (task.state) {
        0 -> painterRes = painterResource(R.drawable.check_state_0)
        1 -> painterRes = painterResource(R.drawable.check_state_1)
        2 -> painterRes = painterResource(R.drawable.check_state_2)
      }

      Icon(
        modifier = Modifier.size(60.dp),
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
          color = textColor
        )
      }

      // Title
      Text(
        text = task.title,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor,
        textDecoration = titleDecoration
      )
    }

    // Priority indicator
    val priorityColor = when (task.priority) {
      1 -> PriorityRed
      2 -> PriorityOrange
      3 -> PriorityYellow
      else -> Color.Transparent
    }

    var painterRes = painterResource(R.drawable.bookmark_short)

    when (task.state) {
      0 -> painterRes = painterResource(R.drawable.bookmark_short)
      1 -> painterRes = painterResource(R.drawable.bookmark_short)
      2 -> painterRes = painterResource(R.drawable.bookmark_long)
    }

    Icon(
      modifier = Modifier
        .size(60.dp)
        .align(Alignment.Top)
        .offset(y = (-10).dp)
        .padding(end = 10.dp),
      painter = painterRes,
      contentDescription = "Priorität",
      tint = priorityColor,
    )
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
    thickness = 1.dp,
    color = MaterialTheme.colorScheme.onSecondary
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
      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor
      )
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
    timeString = task.duration.toString() + " min"
  }

  return timeString
}

