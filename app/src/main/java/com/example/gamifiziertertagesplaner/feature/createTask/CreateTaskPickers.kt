package com.example.gamifiziertertagesplaner.feature.createTask

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.PriorityOrange
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.example.gamifiziertertagesplaner.ui.theme.PriorityYellow
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Reminder Picker
 *
 * @param state          The current state of the reminder picker
 * @param onStateChange  The callback to be invoked when the state changes
 */
@Composable
fun ReminderPicker(
  modifier: Modifier,
  state: Int,
  onStateChange: (Int) -> Unit,
  enabled: Boolean
) {
  val options = listOf(0, 5, 10, 30, 60)

  ExpandableRadioPicker(
    modifier = modifier,
    value = state,
    onValueChange = { if (enabled) onStateChange(it) },
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
    tintFor = {
      if (enabled)
        MaterialTheme.colorScheme.onSecondary
      else
        MaterialTheme.colorScheme.surfaceVariant
    },
    enabled = enabled
  )
}

/**
 * Priority Picker
 *
 * @param value         The current value of the priority
 */
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
  iconOnly: Boolean = false,
  enabled: Boolean = true
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
      Row(
        verticalAlignment = Alignment.Top
      ) {
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
            color = tintFor(value)
          )
        }
      }

      // Options
      if (expanded && enabled) {
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
      .padding(top = 12.dp)
      .clickable(onClick = onClick),
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