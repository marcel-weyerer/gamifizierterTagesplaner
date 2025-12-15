package com.example.gamifiziertertagesplaner.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.notifications.DailyReminderScheduler

enum class SettingsType {
  USERNAME,
  PROFILE_PICTURE,
  PASSWORD,
  EMAIL,
  NOTIFICATION,
  ENDOFDAY
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
fun EndOfDayContent(
  onOpenEndOfDay: () -> Unit
) {
  ActionButton(
    onClick = onOpenEndOfDay,
    modifier = Modifier
      .padding(top = 24.dp)
      .fillMaxWidth(),
    text = "Tagesabschluss anpassen"
  )
}

@Composable
fun NotificationContent(
  onOpenDailyReminder: () -> Unit
) {
  val context = LocalContext.current
  var checked1 by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    checked1 = DailyReminderScheduler.isEnabled(context)
  }

  var checked2 by remember { mutableStateOf(true) }
  var checked3 by remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ActionButton(
        onClick = onOpenDailyReminder,
        modifier = Modifier
          .weight(1f),
        text = "Liste anlegen",
        enabled = checked1
      )

      Switch(
        modifier = Modifier.padding(start = 12.dp),
        checked = checked1,
        onCheckedChange = { newValue ->
          checked1 = newValue

          val minutes = DailyReminderScheduler.savedMinutes(context) // or minutesInitial
          if (newValue) {
            // schedule using the currently saved time
            DailyReminderScheduler.scheduleDailyReminder(context, minutes)
          } else {
            // cancel + mark disabled in prefs (your cancel does that)
            DailyReminderScheduler.cancelDailyReminder(context)
          }
        },
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.secondary,
          checkedTrackColor = MaterialTheme.colorScheme.primary,
          uncheckedThumbColor = MaterialTheme.colorScheme.primary,
          uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
        )
      )
    }

    Row(
      modifier = Modifier
        .padding(top = 12.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ActionButton(
        onClick = {},
        modifier = Modifier
          .weight(1f),
        text = "Startzeit",
        enabled = checked2
      )

      Switch(
        modifier = Modifier.padding(start = 12.dp),
        checked = checked2,
        onCheckedChange = {
          checked2 = !checked2
        },
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.secondary,
          checkedTrackColor = MaterialTheme.colorScheme.primary,
          uncheckedThumbColor = MaterialTheme.colorScheme.primary,
          uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
        )
      )
    }

    Row(
      modifier = Modifier
        .padding(top = 12.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      ActionButton(
        onClick = {},
        modifier = Modifier
          .weight(1f),
        text = "Erinnerung",
        enabled = checked3
      )

      Switch(
        modifier = Modifier.padding(start = 12.dp),
        checked = checked3,
        onCheckedChange = {
          checked3 = !checked3
        },
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.secondary,
          checkedTrackColor = MaterialTheme.colorScheme.primary,
          uncheckedThumbColor = MaterialTheme.colorScheme.primary,
          uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
        )
      )
    }
  }
}