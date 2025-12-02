package com.example.gamifiziertertagesplaner.feature.createTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.firestore.Task
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.example.gamifiziertertagesplaner.ui.theme.PriorityOrange
import com.example.gamifiziertertagesplaner.ui.theme.PriorityYellow
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale


private val cornerRadius = 48.dp
private val shadowElevation = 3.dp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
  viewModel: CreateTaskViewModel = viewModel(),
  onCancel: () -> Unit,
  taskToEdit: Task? = null
) {
  val isEdit = taskToEdit != null   // Should a new task be created or an existing one be edited
  val scrollState = rememberScrollState()

  // Text field states
  val titleState = remember(taskToEdit?.id) {
    TextFieldState(taskToEdit?.title ?: "")
  }
  val descriptionState = remember(taskToEdit?.id) {
    TextFieldState(taskToEdit?.description ?: "")
  }

  // Priority state
  val priorityState = rememberSaveable(taskToEdit?.id) {
    mutableIntStateOf(taskToEdit?.priority ?: 2)
  }


  // Time and reminder states
  val initialStartLocalTime = remember(taskToEdit?.id) {
    if (taskToEdit?.startTime != null) {
      Instant.ofEpochSecond(
        taskToEdit.startTime.seconds,
        taskToEdit.startTime.nanoseconds.toLong()
      )
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    } else {
      LocalTime.of(10, 0)
    }
  }

  val timePickerState = rememberTimePickerState(
    initialHour = initialStartLocalTime.hour,
    initialMinute = initialStartLocalTime.minute,
    is24Hour = true,
  )

  val durationState = rememberTimePickerState(
    initialHour = (taskToEdit?.duration ?: 0) / 60,
    initialMinute = (taskToEdit?.duration ?: 0) % 60,
    is24Hour = true,
  )

  var reminderState by rememberSaveable(taskToEdit?.id) {
    mutableIntStateOf(taskToEdit?.reminder ?: 10)
  }

  Surface(    // background
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)
        .verticalScroll(scrollState)
    ) {
      // Page title
      Text(
        modifier = Modifier
          .padding(top = 24.dp)
          .align(Alignment.CenterHorizontally),
        text = if (isEdit) "Task bearbeiten" else "Task hinzufügen",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.surfaceVariant
      )

      // Task title input
      SectionHeader("Titel + Priorität")
      Row(
        modifier = Modifier.fillMaxWidth()
      ) {
        TextInputField(
          modifier = Modifier.weight(1f),
          state = titleState,
          maxHeightLines = 2,
          placeholder = "Titel"
        )

        Spacer(modifier = Modifier.width(12.dp))

        PriorityPicker(
          modifier = Modifier.align(Alignment.CenterVertically),
          value = priorityState.intValue,
          onValueChange = { newValue -> priorityState.intValue = newValue }
        )
      }

      // Task description input
      SectionHeader("Beschreibung")
      TextInputField(
        modifier = Modifier.fillMaxWidth(),
        state = descriptionState,
        maxHeightLines = 4,
        placeholder = "Beschreibung"
      )

      // Task times input
      SectionHeader("Startzeit + Dauer")
      TimesInputBox(
        timePickerState = timePickerState,
        durationState = durationState,
        onStartTimeChange = { newHour, newMinute ->
          timePickerState.hour = newHour
          timePickerState.minute = newMinute
        },
        onDurationChange = { newHour, newMinute ->
          durationState.hour = newHour
          durationState.minute = newMinute
        }
      )

      // Task reminder input
      SectionHeader("Erinnerung")
      ReminderPicker(
        modifier = Modifier.fillMaxWidth(),
        state = reminderState,
        onStateChange = { newValue -> reminderState = newValue }
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Cancel button
        ActionButton(
          onClick = onCancel,
          text = "Abbrechen"
        )

        // Save button
        ActionButton(
          onClick = {
            val title = titleState.text.toString().ifEmpty { "Neuer Task" }
            val description = descriptionState.text.toString().takeIf { it != "" }
            val duration = (durationState.hour * 60 + durationState.minute).takeIf { it != 0 }
            val reminder = reminderState.takeIf { it != 0 }

            if (isEdit) {
              // UPDATE existing task
              viewModel.updateTask(
                id = taskToEdit.id,
                title = title,
                priority = priorityState.intValue,
                description = description,
                startTime = createTimeStamp(timePickerState),
                duration = duration,
                reminder = reminder,
                state = taskToEdit.state
              )
            } else {
              // CREATE new task
              viewModel.addTask(
                title = title,
                priority = priorityState.intValue,
                description = description,
                startTime = createTimeStamp(timePickerState),
                duration = duration,
                reminder = reminder
              )
            }

            onCancel()    // Return to home screen
          },
          text = "Speichern",
          isPrimary = true
        )
      }
    }
  }
}

/**
 * Reusable text input field
 *
 * @param state          The state of the text field
 * @param maxHeightLines The maximum number of lines the text field should have
 * @param placeholder    The text to display in the text field
 */
@Composable
private fun TextInputField(
  modifier: Modifier = Modifier,
  state: TextFieldState,
  maxHeightLines: Int,
  placeholder: String
) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation
  ) {
    TextField(
      state = state,
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      textStyle = MaterialTheme.typography.bodyMedium,
      placeholder = {
        Text(
          text = placeholder,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.surfaceVariant
        )
      },
      lineLimits = TextFieldLineLimits.MultiLine(
        minHeightInLines = 1,
        maxHeightInLines = maxHeightLines
      ),
      colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.onSecondary,
        unfocusedTextColor = MaterialTheme.colorScheme.onSecondary
      )
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimesInputBox(
  timePickerState: TimePickerState,
  durationState: TimePickerState,
  onStartTimeChange: (Int, Int) -> Unit,
  onDurationChange: (Int, Int) -> Unit
) {
  var showStartTimePicker by remember { mutableStateOf(false) }
  var showDurationPicker by remember { mutableStateOf(false) }

  val startTimeString = String.format(
    Locale.getDefault(),
    "%02d:%02d",
    timePickerState.hour,
    timePickerState.minute
  )
  val durationString = "${durationState.hour * 60 + durationState.minute}  Minuten"

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
        .animateContentSize()
    ) {
      // Start time input
      TimeInputField(
        onClick = { showStartTimePicker = !showStartTimePicker },
        icon = Icons.Default.AccessTime,
        timeString = startTimeString
      )

      HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
      )

      // Duration input
      TimeInputField(
        onClick = { showDurationPicker = !showDurationPicker },
        icon = Icons.Default.Timelapse,
        timeString = durationString
      )

      if (showStartTimePicker) {
        DialTimePicker(
          timePickerState = timePickerState,
          title = "Uhrzeit",
          onDismiss = { showStartTimePicker = false },
          onConfirm = {
            onStartTimeChange(timePickerState.hour, timePickerState.minute)
            showStartTimePicker = false
          }
        )
      }

      if (showDurationPicker) {
        DialTimePicker(
          timePickerState = durationState,
          title = "Dauer",
          onDismiss = { showDurationPicker = false },
          onConfirm = {
            onDurationChange(durationState.hour, durationState.minute)
            showDurationPicker = false
          }
        )
      }
    }
  }
}

@Composable
private fun TimeInputField(onClick: () -> Unit, icon: ImageVector, timeString: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(12.dp),
    verticalAlignment = Alignment.Top
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSecondary,
      modifier = Modifier.size(24.dp)
    )

    Spacer(Modifier.width(12.dp))

    Text(
      text = timeString,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSecondary
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
private fun DialTimePicker(
  timePickerState: TimePickerState,
  title: String,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    title = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.secondary
        )
      }
    },
    onDismissRequest = onDismiss,
    confirmButton = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        TextButton(onClick = onDismiss) {
          Text(
            text = "Abbrechen",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
          )
        }
        TextButton(onClick = onConfirm) {
          Text(
            text = "OK",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
          )
        }
      }
    },
    dismissButton = {},
    text = {
      TimePicker(
        state = timePickerState,
        colors = TimePickerDefaults.colors(
          clockDialColor = MaterialTheme.colorScheme.secondary,
          selectorColor = MaterialTheme.colorScheme.surface,
          clockDialSelectedContentColor = MaterialTheme.colorScheme.secondary,
          clockDialUnselectedContentColor = MaterialTheme.colorScheme.surface,
          timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.secondary,
          timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onSecondary,
          timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.surface
  )
}

/**
 * Reminder Picker
 *
 * @param state          The current state of the reminder picker
 * @param onStateChange  The callback to be invoked when the state changes
 */
@Composable
private fun ReminderPicker(modifier: Modifier, state: Int, onStateChange: (Int) -> Unit) {
  val options = listOf(0, 5, 10, 30, 60)

  ExpandableRadioPicker(
    modifier = modifier,
    value = state,
    onValueChange = onStateChange,
    options = options,
    labelFor = { minutes ->
      when (minutes) {
        0 -> "Keine Erinnerung"
        5 -> "5 min vorher"
        10 -> "10 min vorher"
        30 -> "30 min vorher"
        60 -> "1 Std vorher"
        else -> "$minutes min vorher"
      }
    },
    iconFor = { minutes ->
      if (minutes == 0) Icons.Default.NotificationsOff
      else Icons.Default.NotificationsActive
    },
    tintFor = { MaterialTheme.colorScheme.onSecondary }
  )
}

@Composable
fun PriorityPicker(
  modifier: Modifier,
  value: Int,
  onValueChange: (Int) -> Unit
) {
  val options = listOf(1, 2, 3)

  ExpandableRadioPicker(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    options = options,
    labelFor = { priority ->
      when (priority) {
        1 -> "Hoch"
        2 -> "Mittel"
        3 -> "Niedrig"
        else -> ""
      }
    },
    iconFor = { priority -> Icons.Default.Circle },
    tintFor = { priority ->
      when (priority) {
        1 -> PriorityRed
        2 -> PriorityOrange
        3 -> PriorityYellow
        else -> MaterialTheme.colorScheme.onSecondary
      }
    },
    iconOnly = true
  )
}

@Composable
private fun <T> ExpandableRadioPicker(
  modifier: Modifier,
  value: T,
  onValueChange: (T) -> Unit,
  options: List<T>,
  labelFor: (T) -> String,
  iconFor: (T) -> ImageVector,
  tintFor: @Composable (T) -> Color,
  iconOnly: Boolean = false
) {
  var expanded by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier,
    onClick = { expanded = !expanded },
    color = MaterialTheme.colorScheme.secondary,
    shape = RoundedCornerShape(cornerRadius),
    shadowElevation = shadowElevation
  ) {
    Column(
      modifier = Modifier
        .padding(24.dp)
        .animateContentSize()
    ) {
      // Header row
      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = iconFor(value),
          contentDescription = null,
          tint = tintFor(value),
          modifier = Modifier.size(24.dp)
        )

        if (!iconOnly) {
          Spacer(Modifier.width(12.dp))

          Text(
            text = labelFor(value),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondary
          )
        }
      }

      // Options
      if (expanded) {
        Column {
          HorizontalDivider(
            modifier = Modifier
              .padding(top = 24.dp, bottom = 12.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSecondary
          )

          options.forEach { option ->
            PickerOptionRow(
              text = labelFor(option),
              selected = option == value,
              onClick = {
                onValueChange(option)
                expanded = false
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun PickerOptionRow(
  text: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(
      selected = selected,
      onClick = onClick
    )

    Spacer(Modifier.width(12.dp))

    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSecondary
    )
  }
}


/**
 * Reusable action button
 *
 * @param onClick        The callback to be invoked when the button is clicked
 * @param text           The text to be displayed on the button
 * @param isPrimary      Whether the button should be primary or secondary
 */
@Composable
private fun ActionButton(onClick: () -> Unit, text: String, isPrimary: Boolean = false) {
  var containerColor: Color
  var contentColor: Color

  if (isPrimary) {
    containerColor = MaterialTheme.colorScheme.primary
    contentColor = MaterialTheme.colorScheme.onPrimary
  } else {
    containerColor = MaterialTheme.colorScheme.secondary
    contentColor = MaterialTheme.colorScheme.onSecondary
  }

  ElevatedButton(
    onClick = onClick,
    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
    shape = RoundedCornerShape(cornerRadius),
    elevation = ButtonDefaults.elevatedButtonElevation(
      defaultElevation = shadowElevation,
      pressedElevation = 0.dp,
    ),
    colors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
fun createTimeStamp(timePickerState: TimePickerState): Timestamp {
  val today = LocalDate.now()
  val localDateTime = LocalDateTime.of(
    today,
    LocalTime.of(timePickerState.hour, timePickerState.minute)
  )

  val instant = localDateTime
    .atZone(ZoneId.systemDefault())
    .toInstant()

  return Timestamp(instant.epochSecond, instant.nano)
}