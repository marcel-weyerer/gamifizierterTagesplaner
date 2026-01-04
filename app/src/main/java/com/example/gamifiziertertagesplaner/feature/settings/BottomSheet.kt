package com.example.gamifiziertertagesplaner.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.components.ActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
  sheetState: SheetState,
  onSave: () -> Unit,
  onCancel: () -> Unit,
  title: String,
  description: String,
  content: @Composable () -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onCancel,
    sheetState = sheetState,
    dragHandle = null,
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 48.dp, horizontal = 24.dp)
    ) {
        Text(
          modifier = Modifier.fillMaxWidth(),
          text = title,
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Center
        )

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp),
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.surfaceVariant,
        textAlign = TextAlign.Center
      )

      content()

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        ActionButton(
          onClick = onCancel,
          modifier = Modifier.width(150.dp),
          text = "Abbrechen"
        )

        ActionButton(
          onClick = onSave,
          modifier = Modifier.width(150.dp),
          text = "Speichern",
          isPrimary = true
        )
      }
    }
  }
}