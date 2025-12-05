package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Reusable text input field
 *
 * @param state          The state of the text field
 * @param maxHeightLines The maximum number of lines the text field should have
 * @param placeholder    The text to display in the text field
 */
@Composable
fun TextInputField(
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