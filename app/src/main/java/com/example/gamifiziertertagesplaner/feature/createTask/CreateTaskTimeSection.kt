package com.example.gamifiziertertagesplaner.feature.createTask

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.DialTimePicker
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation
import java.util.Locale

/**
 * Times Input Box
 *
 * @param timePickerState       The state of the start time picker
 * @param durationState         The state of the duration picker
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimesInputBox(
  timePickerState: TimePickerState,
  durationState: TimePickerState,
  isStartTimeSet: Boolean,
  isDurationSet: Boolean,
  onStartTimeSetChange: (Boolean) -> Unit,
  onDurationSetChange: (Boolean) -> Unit,
  onStartTimeChange: (Int, Int) -> Unit,
  onDurationChange: (Int, Int) -> Unit
) {
  var showStartTimePicker by remember { mutableStateOf(false) }
  var showDurationPicker by remember { mutableStateOf(false) }

  val startTimeString = if (isStartTimeSet) {
    String.format(
      Locale.getDefault(),
      "%02d:%02d",
      timePickerState.hour,
      timePickerState.minute
    )
  } else {
    "Startzeit"
  }
  val durationString = if (isDurationSet) {
    String.format(
      Locale.getDefault(),
      "%02dh %02dmin",
      durationState.hour,
      durationState.minute
    )
  } else {
    "Dauer"
  }

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
        timeString = startTimeString,
        isTimeSet = isStartTimeSet,
        painterResource = painterResource(R.drawable.xmark),
        onDelete = { onStartTimeSetChange(false) }
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
        timeString = durationString,
        isTimeSet = isDurationSet,
        painterResource = painterResource(R.drawable.xmark),
        onDelete = { onDurationSetChange(false) }
      )

      if (showStartTimePicker) {
        DialTimePicker(
          timePickerState = timePickerState,
          title = "Uhrzeit",
          onDismiss = { showStartTimePicker = false },
          onConfirm = {
            onStartTimeChange(timePickerState.hour, timePickerState.minute)
            onStartTimeSetChange(true)
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
            onDurationSetChange(true)
            showDurationPicker = false
          }
        )
      }
    }
  }
}

/**
 * Time Input Field
 *
 * @param onClick       The callback to be invoked when the input field is clicked
 */
@Composable
private fun TimeInputField(
  onClick: () -> Unit,
  icon: ImageVector,
  timeString: String,
  isTimeSet: Boolean = false,
  painterResource: Painter? = null,
  onDelete: (() -> Unit)? = null
) {
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
      modifier = Modifier.weight(1f),
      text = timeString,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSecondary
    )

    if (isTimeSet && painterResource != null && onDelete != null) {
      IconButton(
        onClick = onDelete,
        modifier = Modifier.size(24.dp)
      ) {
        Icon(
          painter = painterResource,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSecondary
        )
      }
    }
  }
}