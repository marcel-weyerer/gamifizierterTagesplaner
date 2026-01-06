package com.example.gamifiziertertagesplaner.feature.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.TextInputField
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import kotlinx.coroutines.delay

@Composable
fun SignUpScreen(
  authViewModel: AuthViewModel,
  onSignedUp: () -> Unit,
  onBackToLogin: () -> Unit
) {
  val context = LocalContext.current

  // Input states
  val usernameState = remember { TextFieldState() }
  val emailState = remember { TextFieldState() }
  val passwordState = remember { TextFieldState() }
  var profileImageBytes by remember { mutableStateOf<ByteArray?>(null) }
  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    selectedImageUri = uri
    profileImageBytes = uri?.let {
      try {
        context.contentResolver.openInputStream(it)?.use { stream ->
          stream.readBytes()
        }
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  val isLoading = authViewModel.isLoading
  val error = authViewModel.errorMessage

  // Hide error message after given time
  LaunchedEffect(error) {
    if (error != null) {
      delay(2000)
      authViewModel.clearError()
    }
  }

  // Background
  Surface(
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

      // User name input
      TextInputField(
        state = usernameState,
        maxHeightLines = 1,
        placeholder = "Nutzername"
      )

      Spacer(Modifier.height(12.dp))

      // E-Mail input
      TextInputField(
        state = emailState,
        maxHeightLines = 1,
        placeholder = "E-Mail"
      )

      Spacer(Modifier.height(12.dp))

      // Password input
      TextInputField(
        state = passwordState,
        maxHeightLines = 1,
        placeholder = "Passwort",
        hideInput = true
      )

      Spacer(Modifier.height(12.dp))

      // Profile picture input
      ActionButton(
        onClick = { imagePickerLauncher.launch("image/*") },
        modifier = Modifier.fillMaxWidth(),
        text = if (selectedImageUri != null) "Profilbild gewählt" else "Profilbild wählen"
      )

      // Error message
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp),
        contentAlignment = Alignment.Center
      ) {
        androidx.compose.animation.AnimatedVisibility(
          visible = error != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Text(
            text = "Angaben fehlerhaft",
            style = MaterialTheme.typography.bodySmall,
            color = PriorityRed
          )
        }
      }

      // Sign-up button
      ActionButton(
        onClick = {
          val username = usernameState.text.toString()
          val email = emailState.text.toString()
          val password = passwordState.text.toString()

          authViewModel.register(
            email = email,
            password = password,
            username = username,
            profileImageBytes = profileImageBytes,
            onSuccess = onSignedUp
          )
        },
        modifier = Modifier.width(150.dp),
        text = if (isLoading) "..." else "Sign up"
      )

      Spacer(Modifier.height(16.dp))

      // Back to login button
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
