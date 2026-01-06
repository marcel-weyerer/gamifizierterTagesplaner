package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable time picker dialog
 *
 * @param timePickerState    The state of the time picker
 * @param title              The title of the dialog
 * @param onDismiss          The callback to be invoked when the dialog is dismissed
 * @param onConfirm          The callback to be invoked when the dialog is confirmed
 */
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun DialTimePicker(
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