package com.example.gamifiziertertagesplaner.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.TextInputField
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed

@Composable
fun SignUpScreen(
  authViewModel: AuthViewModel = viewModel(),
  onSignedUp: () -> Unit,
  onBackToLogin: () -> Unit
) {
  val usernameState = remember { TextFieldState() }
  val emailState = remember { TextFieldState() }
  val passwordState = remember { TextFieldState() }

  val isLoading = authViewModel.isLoading
  val error = authViewModel.errorMessage

  Surface(    // background
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Page title
      Text(
        text = "Account erstellen",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.surfaceVariant,
        textAlign = TextAlign.Center
      )

      Spacer(Modifier.height(48.dp))

      TextInputField(
        state = usernameState,
        maxHeightLines = 1,
        placeholder = "Nutzername"
      )

      Spacer(Modifier.height(16.dp))

      TextInputField(
        state = emailState,
        maxHeightLines = 1,
        placeholder = "E-Mail"
      )

      Spacer(Modifier.height(16.dp))

      TextInputField(
        state = passwordState,
        maxHeightLines = 1,
        placeholder = "Passwort"
      )

      if (error != null) {
        Spacer(Modifier.height(12.dp))
        Text(
          text = "Angaben falsch",
          style = MaterialTheme.typography.bodySmall,
          color = PriorityRed)
      }

      Spacer(Modifier.height(24.dp))

      ActionButton(
        onClick = {
          val username = usernameState.text.toString()
          val email = emailState.text.toString()
          val password = passwordState.text.toString()

          authViewModel.register(
            email = email,
            password = password,
            username = username,
            profileImageBytes = null,
            onSuccess = onSignedUp
          )
        },
        modifier = Modifier.width(150.dp),
        text = if (isLoading) "..." else "Sign up"
      )

      Spacer(Modifier.height(16.dp))

      TextButton(
        onClick = onBackToLogin
      ) {
        Text(
          text = "Schon einen Account? Login",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.surfaceVariant
        )
      }
    }
  }
}
