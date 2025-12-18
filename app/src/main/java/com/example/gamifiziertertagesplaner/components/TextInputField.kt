package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Reusable text input field
 *
 * @param state          The state of the text field
 * @param maxHeightLines The maximum number of lines the text field should have
 * @param placeholder    The text to display in the text field
 * @param hideInput      Whether to hide the input (for passwords)
 */
@Composable
fun TextInputField(
  modifier: Modifier = Modifier,
  state: TextFieldState,
  maxHeightLines: Int = 1,
  placeholder: String,
  hideInput: Boolean = false
) {
  // Hide input (for passwords)
  var isHidden by remember(hideInput) { mutableStateOf(hideInput) }

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
      outputTransformation = if (hideInput && isHidden) {
        OutputTransformation {
          for (i in 0 until length) {
            replace(i, i + 1, "●")
          }
        }
      } else null,
      trailingIcon = {
        if (hideInput) {
          IconButton(onClick = { isHidden = !isHidden }) {
            Icon(
              painter =
                if (isHidden)
                  painterResource(R.drawable.eye_slash)
                else
                  painterResource(R.drawable.eye),
              contentDescription = if (isHidden) "Show input" else "Hide input",
              tint = MaterialTheme.colorScheme.onSecondary
            )
          }
        }
      },
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