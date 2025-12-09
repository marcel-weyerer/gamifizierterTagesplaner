package com.example.gamifiziertertagesplaner.feature.endOfDay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.TextInputField
import com.example.gamifiziertertagesplaner.ui.theme.Cream
import com.example.gamifiziertertagesplaner.ui.theme.DarkBrown
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown

@Composable
fun EndOfDayScreen(
  onOpenHome: () -> Unit
) {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White),
  ) {
    // Circle background
    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            color = Cream,
            radius = (maxHeight / 3f).toPx(),
            center = Offset(x = size.width / 2f, y = size.height / 2f + 200)
          )
        }
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            color = MediumBrown,
            radius = (maxHeight / 3f).toPx(),
            center = Offset(x = size.width / 2f, y = size.height / 3f + 175)
          )
        }
    )

    Box(
      modifier = Modifier
        .fillMaxSize()
        .drawBehind {
          drawCircle(
            color = DarkBrown,
            radius = (maxHeight / 3f).toPx(),
            center = Offset(x = size.width / 2f, y = 0f)
          )
        }
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 48.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Tag erfolgreich\nbeendet!",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onPrimary,
          textAlign = TextAlign.Center,
        )
      }

      Spacer(modifier = Modifier.height(100.dp))

      TextInputField(
        state = rememberTextFieldState(""),
        maxHeightLines = 1,
        placeholder = "E-Mail"
      )

      Spacer(modifier = Modifier.height(24.dp))

      TextInputField(
        state = rememberTextFieldState(""),
        maxHeightLines = 1,
        placeholder = "Passwort"
      )

      Spacer(modifier = Modifier.height(32.dp))

      ActionButton(
        onClick = onOpenHome,
        modifier = Modifier.width(150.dp),
        text = "Login"
      )

      Spacer(modifier = Modifier.height(50.dp))

      Text(
        text = "Noch kein Account?",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(6.dp))

      ActionButton(
        onClick = {},
        modifier = Modifier.width(150.dp),
        text = "Tag beenden"
      )
    }
  }
}