package com.example.gamifiziertertagesplaner.feature.login

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.TextInputField
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.Cream
import com.example.gamifiziertertagesplaner.ui.theme.DarkBrown
import com.example.gamifiziertertagesplaner.ui.theme.MediumBrown
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
  authViewModel: AuthViewModel,
  onLoggedIn: () -> Unit,
  onSignUp: () -> Unit
) {
  val emailState = remember { TextFieldState() }
  val passwordState = remember { TextFieldState() }

  val isLoading = authViewModel.isLoading
  val error = authViewModel.errorMessage

  // Hide error message after give time
  LaunchedEffect(error) {
    if (error != null) {
      delay(2000)
      authViewModel.clearError()
    }
  }

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
          text = "Willkommen\nzurück!",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onPrimary,
          textAlign = TextAlign.Center,
        )
      }

      Spacer(modifier = Modifier.height(100.dp))

      TextInputField(
        state = emailState,
        maxHeightLines = 1,
        placeholder = "E-Mail"
      )

      Spacer(modifier = Modifier.height(24.dp))

      TextInputField(
        state = passwordState,
        maxHeightLines = 1,
        placeholder = "Passwort"
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(20.dp),
        contentAlignment = Alignment.Center
      ) {
        androidx.compose.animation.AnimatedVisibility(
          visible = error != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Text(
            text = "E-Mail oder Passwort falsch",
            style = MaterialTheme.typography.bodySmall,
            color = PriorityRed
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      ActionButton(
        onClick = {
          val email = emailState.text.toString()
          val password = passwordState.text.toString()
          authViewModel.login(email, password, onLoggedIn)
        },
        modifier = Modifier.width(150.dp),
        text = if (isLoading) "..." else "Login"
      )

      Spacer(modifier = Modifier.height(50.dp))

      Text(
        text = "Noch kein Account?",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(6.dp))

      ActionButton(
        onClick = onSignUp,
        modifier = Modifier.width(150.dp),
        text = "Sign up"
      )
    }
  }
}