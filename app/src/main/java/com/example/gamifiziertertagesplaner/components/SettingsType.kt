package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
fun rememberSettingsContentMap(): Map<SettingsType, SettingsContent> {
  return remember {
    mapOf(
      SettingsType.USERNAME to SettingsContent(
        title = "Nutzername ändern",
        description = "Gib einen neuen Benutzernamen ein",
        content = { UsernameContent() }
      ),
      SettingsType.PROFILE_PICTURE to SettingsContent(
        title = "Profilbild ändern",
        description = "Möchtest du dein Profilbild ändern?",
        content = { ProfilePictureContent() }
      ),
      SettingsType.PASSWORD to SettingsContent(
        title = "Passwort ändern",
        description = "Gib dein neues Passwort ein",
        content = { PasswordContent() }
      ),
      SettingsType.EMAIL to SettingsContent(
        title = "E-Mail ändern",
        description = "Gib deine neue E-Mail-Adresse ein",
        content = { EmailContent() }
      ),
      SettingsType.NOTIFICATION to SettingsContent(
        title = "Benachrichtigungen anpassen",
        description = "Passe deine Benachrichtigungen an",
        content = { NotificationContent() }
      )
    )
  }
}

@Composable
fun UsernameContent() {
  TextInputField(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp),
    state = TextFieldState(),
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
fun PasswordContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    TextInputField(
      modifier = Modifier.fillMaxWidth(),
      state = TextFieldState(),
      placeholder = "Aktuelles Passwort"
    )
    TextInputField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.dp),
      state = TextFieldState(),
      placeholder = "Neues Passwort"
    )
    TextInputField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      state = TextFieldState(),
      placeholder = "Neues Passwort"
    )
  }
}

@Composable
fun EmailContent() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    TextInputField(
      modifier = Modifier.fillMaxWidth(),
      state = TextFieldState(),
      placeholder = "Neue E-Mail"
    )
    TextInputField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp),
      state = TextFieldState(),
      placeholder = "Neue E-Mail"
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