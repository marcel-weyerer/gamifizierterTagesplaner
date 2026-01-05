package com.example.gamifiziertertagesplaner.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.components.TaskProgressBar
import com.example.gamifiziertertagesplaner.firestore.Task
import kotlinx.coroutines.delay

/**
 * Custom composable for the task list.
 *
 * @param circleOffsetY   Vertical offset of the circle.
 * @param innerPadding    Padding for the inner content.
 * @param viewModel       ViewModel for the Home screen.
 * @param onOpenEditTask  Callback invoked when an edit button is pressed.
 */
@Composable
fun TaskList(
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
            val (doneTasks, activeTasksAll) = sortedTasks.partition { it.state == 0 }

            val millisNow = rememberMillisNow()   // current time in millis
            val highlightPeriod = 10 * 60_000L      // 10 minutes in milliseconds

            // Move tasks that start soon to the top
            // Split active tasks into starting soon and active tasks
            val (startingSoonTasks, activeTasks) = activeTasksAll.partition { task ->
              val start = task.startTime ?: return@partition false
              val startMillis = start.seconds * 1000L + start.nanoseconds / 1_000_000L

              // include tasks that start in the next 10 minutes and keep them for 10 minutes after the start time
              millisNow >= (startMillis - highlightPeriod) && millisNow <= (startMillis + highlightPeriod)
            }

            // Sort starting soon tasks by priority and then by starting time
            val sortedStartingSoonTasks = startingSoonTasks.sortedWith(
              compareBy(
                { it.priority },
                { it.startTime == null },
                { it.startTime?.seconds ?: Long.MAX_VALUE },
                { it.startTime?.nanoseconds ?: Int.MAX_VALUE }
              )
            )

            // Build priority groups only from active tasks that do not start soon
            // Sort them by priority first and then by start time
            val priorityGroups = listOf(1, 2, 3).mapNotNull { priority ->
              val groupTasks = activeTasks
                .filter { it.priority == priority }
                .sortedWith(
                  compareBy(
                    { it.startTime == null },
                    { it.startTime?.seconds ?: Long.MAX_VALUE },
                    { it.startTime?.nanoseconds ?: Int.MAX_VALUE }
                  )
                )

              if (groupTasks.isEmpty())
                null
              else
                priority to groupTasks
            }


            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .align(Alignment.TopCenter),
            ) {
              // Section for starting soon tasks
              if (sortedStartingSoonTasks.isNotEmpty()) {
                item { SectionHeader(text = "Beginnt bald") }

                items(
                  items = sortedStartingSoonTasks,
                  key = { it.id }
                ) { task ->
                  TaskView(viewModel, task) { onOpenEditTask(task) }
                  Spacer(Modifier.height(10.dp))
                }
              }

              // Sections for remaining active tasks depending on priority
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

@Composable
private fun rememberMillisNow(refreshPeriod: Long = 60_000L): Long {
  var now by remember { mutableLongStateOf(System.currentTimeMillis()) }

  LaunchedEffect(refreshPeriod) {
    while (true) {
      now = System.currentTimeMillis()
      delay(refreshPeriod)
    }
  }

  return now
}