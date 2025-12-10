package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.ui.theme.cornerRadius
import com.example.gamifiziertertagesplaner.ui.theme.shadowElevation

/**
 * Reusable action button
 *
 * @param onClick        The callback to be invoked when the button is clicked
 * @param text           The text to be displayed on the button
 * @param isPrimary      Whether the button should be primary or secondary
 */
@Composable
fun ActionButton(
  onClick: () -> Unit,
  modifier: Modifier,
  text: String,
  leadingIcon: Painter? = null,
  isPrimary: Boolean = false
) {
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
    modifier = modifier,
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
    Row {
      if (leadingIcon != null) {
        Icon(
          modifier = Modifier.size(20.dp),
          painter = leadingIcon,
          contentDescription = "subtract"
        )

        Spacer(modifier = Modifier.width(12.dp))
      }

      Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}