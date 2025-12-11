package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SettingsType {
  USERNAME,
  PROFILE_PICTURE,
  PASSWORD,
  EMAIL,
  NOTIFICATION
}

data class SettingsContent(
  val title: String,
  val description: String,
  val content: @Composable () -> Unit
)

@Composable
fun UsernameContent(
  state: TextFieldState
) {
  TextInputField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp),
    state = state,
    placeholder = "Neuer Name"
  )
}


@Composable
fun ProfilePictureContent() {
  ActionButton(
    onClick = {},
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp),
    text = "Profilbild auswählen"
  )
}


@Composable
fun PasswordContent(
  newPasswordState: TextFieldState,
  newPasswordConfirmState: TextFieldState
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    TextInputField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.dp),
      state = newPasswordState,
      placeholder = "Neues Passwort"
    )
    TextInputField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      state = newPasswordConfirmState,
      placeholder = "Neues Passwort bestätigen"
    )
  }
}


@Composable
fun EmailContent(
  newEmailState: TextFieldState,
  newEmailConfirmState: TextFieldState
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    TextInputField(
      modifier = Modifier.fillMaxWidth(),
      state = newEmailState,
      placeholder = "Neue E-Mail"
    )
    TextInputField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      state = newEmailConfirmState,
      placeholder = "Neue E-Mail bestätigen"
    )
  }
}

@Composable
fun NotificationContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    ActionButton(
      onClick = {},
      modifier = Modifier
        .fillMaxWidth(),
      text = "Tagesplan Benachrichtigung"
    )

    ActionButton(
      onClick = {},
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      text = "Startzeit Benachrichtigung"
    )

    ActionButton(
      onClick = {},
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      text = "Erinnerung Benachrichtung"
    )
  }
}