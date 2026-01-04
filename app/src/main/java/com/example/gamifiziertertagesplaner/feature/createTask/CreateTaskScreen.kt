package com.example.gamifiziertertagesplaner.feature.createTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.components.TextInputField
import com.example.gamifiziertertagesplaner.firestore.Task
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

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

  var isStartTimeSet by rememberSaveable(taskToEdit?.id) {
    mutableStateOf(taskToEdit?.startTime != null)
  }

  var isDurationSet by rememberSaveable(taskToEdit?.id) {
    mutableStateOf(taskToEdit?.duration != null)
  }

  val durationState = rememberTimePickerState(
    initialHour = (taskToEdit?.duration ?: 0) / 60,
    initialMinute = (taskToEdit?.duration ?: 0) % 60,
    is24Hour = true,
  )

  var reminderState by rememberSaveable(taskToEdit?.id) {
    mutableIntStateOf(taskToEdit?.reminder ?: 0)
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
        isStartTimeSet = isStartTimeSet,
        isDurationSet = isDurationSet,
        onStartTimeSetChange = { isSet ->
          isStartTimeSet = isSet
          if (!isSet) {
            reminderState = 0
          }
        },
        durationState = durationState,
        onDurationSetChange = { isSet ->
          isDurationSet = isSet
          if (!isSet) {
            reminderState = 0
          }
        },
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
        onStateChange = { newValue -> reminderState = newValue },
        enabled = isStartTimeSet
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
          modifier = Modifier.width(150.dp),
          text = "Abbrechen"
        )

        // Save button
        ActionButton(
          onClick = {
            val title = titleState.text.toString().ifEmpty { "Neuer Task" }
            val description = descriptionState.text.toString().takeIf { it != "" }
            val startTime: Timestamp? = if (isStartTimeSet) {
              createTimeStamp(timePickerState)
            } else {
              null
            }
            val duration = if (isDurationSet)
                (durationState.hour * 60 + durationState.minute)
              else
                null
            val reminder = reminderState.takeIf { it != 0 }

            if (isEdit) {
              // UPDATE existing task
              viewModel.updateTask(
                id = taskToEdit.id,
                title = title,
                priority = priorityState.intValue,
                description = description,
                startTime = startTime,
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
                startTime = startTime,
                duration = duration,
                reminder = reminder
              )
            }

            onCancel()    // Return to home screen
          },
          modifier = Modifier.width(150.dp),
          text = "Speichern",
          isPrimary = true
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
private fun createTimeStamp(timePickerState: TimePickerState): Timestamp {
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