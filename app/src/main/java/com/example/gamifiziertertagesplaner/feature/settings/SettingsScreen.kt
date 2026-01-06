package com.example.gamifiziertertagesplaner.feature.settings

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.DialTimePicker
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.notifications.DailyReminderScheduler
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

/**
 * Settings screen
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  authViewModel: AuthViewModel,
  onOpenHome: () -> Unit,
  onOpenPomodoro: () -> Unit,
  onOpenBookshelf: () -> Unit,
  onLoggedOut: () -> Unit
) {
  // Bottom sheet state
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  // Current settings type
  var currentType by remember { mutableStateOf<SettingsType?>(null) }

  // Input states for all profile properties
  val usernameState = remember { TextFieldState() }

  val newEmailState = remember { TextFieldState() }
  val newEmailConfirmState = remember { TextFieldState() }

  val newPasswordState = remember { TextFieldState() }
  val newPasswordConfirmState = remember { TextFieldState() }

  // End of day states
  // Timestamp from Firestore
  val initialEndOfDayTimestamp = authViewModel.userProfile?.endOfDayTime

  // Derive initial hour and minute for the picker
  val (initialHour, initialMinute) = remember(initialEndOfDayTimestamp) {
    if (initialEndOfDayTimestamp != null) {
      val localTime = initialEndOfDayTimestamp.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalTime()

      localTime.hour to localTime.minute
    } else {
      // default 18:00 if not set
      18 to 0
    }
  }

  // TimePicker state based on timestamp
  val endOfDayTimePickerState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = true,
  )

  var selectedEndOfDayTime by remember { mutableStateOf(initialEndOfDayTimestamp) }
  var showEndOfDayTimePicker by remember { mutableStateOf(false) }

  // Notification states
  val initialReminderMinutes = authViewModel.userProfile?.createListReminderMinutes ?: (10 * 60) // default 10:00
  var selectedReminderMinutes by remember { mutableIntStateOf(initialReminderMinutes) }
  var showReminderTimePicker by remember { mutableStateOf(false) }
  val reminderTimePickerState = rememberTimePickerState(
    initialHour = initialReminderMinutes / 60,
    initialMinute = initialReminderMinutes % 60,
    is24Hour = true
  )

  // Map of content values for each setting type
  val settingsContentMap: Map<SettingsType, SettingsContent> = remember {
    mapOf(
      SettingsType.USERNAME to SettingsContent(
        title = "Nutzername ändern",
        description = "Gib einen neuen Benutzernamen ein",
        content = {
          UsernameContent(state = usernameState)
        }
      ),
      SettingsType.PROFILE_PICTURE to SettingsContent(
        title = "Profilbild ändern",
        description = "Möchtest du dein Profilbild ändern?",
        content = {
          ProfilePictureContent()
        }
      ),
      SettingsType.PASSWORD to SettingsContent(
        title = "Passwort ändern",
        description = "Gib dein neues Passwort ein",
        content = {
          PasswordContent(
            newPasswordState = newPasswordState,
            newPasswordConfirmState = newPasswordConfirmState
          )
        }
      ),
      SettingsType.EMAIL to SettingsContent(
        title = "E-Mail ändern",
        description = "Gib deine neue E-Mail-Adresse ein",
        content = {
          EmailContent(
            newEmailState = newEmailState,
            newEmailConfirmState = newEmailConfirmState
          )
        }
      ),
      SettingsType.ENDOFDAY to SettingsContent(
        title = "Tagesabschluss anpassen",
        description = "Passe deine Tagesabschluss Zeit an",
        content = {
          EndOfDayContent(
            onOpenEndOfDay = { showEndOfDayTimePicker = true }
          )
        }
      ),
      SettingsType.NOTIFICATION to SettingsContent(
        title = "Benachrichtigungen anpassen",
        description = "Passe deine Benachrichtigungen an",
        content = {
          NotificationContent(
            onOpenDailyReminder = { showReminderTimePicker = true }
          )
        }
      )
    )
  }

  // Opens bottom sheet that contains the input fields to change a setting
  fun openSheet(type: SettingsType) {
    currentType = type

    // Prefill fields when opening
    when (type) {
      SettingsType.USERNAME -> {
        val currentName = authViewModel.userProfile?.username.orEmpty()
        usernameState.edit {
          replace(0, length, currentName)
        }
      }

      SettingsType.EMAIL -> {
        val currentEmail = authViewModel.userProfile?.email.orEmpty()
        newEmailState.edit {
          replace(0, length, currentEmail)
        }
        newEmailConfirmState.edit {
          replace(0, length, currentEmail)
        }
      }

      SettingsType.PASSWORD -> {
        // Don't show password
        newPasswordState.edit { replace(0, length, "") }
        newPasswordConfirmState.edit { replace(0, length, "") }
      }

      else -> Unit
    }

    scope.launch { sheetState.show() }
  }

  // Closes the bottom sheet
  fun closeSheet() {
    scope.launch {
      sheetState.hide()
    }.invokeOnCompletion {
      currentType = null
    }
  }

  Scaffold(
    bottomBar = {
      CustomBottomAppBar(
        options = listOf(
          BottomAppBarOption(
            icon = painterResource(R.drawable.home),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Home",
            onClick = onOpenHome
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.book),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Bücherregal",
            onClick = onOpenBookshelf
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.gear),
            tint = MaterialTheme.colorScheme.surface,
            contentDescription = "Settings",
            onClick = {}
          ),
          BottomAppBarOption(
            icon = painterResource(R.drawable.timer),
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Pomodoro",
            onClick = onOpenPomodoro
          )
        )
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      val scrollState = rememberScrollState()

      TopScreenTitle(title = "Einstellungen")

      // Settings section
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp)
          .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Change profile picture
        IconButton(
          onClick = { openSheet(SettingsType.PROFILE_PICTURE) },
          modifier = Modifier.size(150.dp)
        ) {
            Icon(
              painter = painterResource(R.drawable.user),
              contentDescription = "Default Profile Icon",
              modifier = Modifier.fillMaxSize(),
              tint = Color.Unspecified
            )
        }

        // User name
        val username = authViewModel.userProfile?.username ?: "User"
        Text(
          modifier = Modifier.padding(vertical = 12.dp),
          text = username,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onBackground
        )

        SectionHeader("Nutzer Profil")

        // Change user name button
        ActionButton(
          onClick = { openSheet(SettingsType.USERNAME) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          text = "Nutzername ändern"
        )

        // Change password button
        ActionButton(
          onClick = { openSheet(SettingsType.PASSWORD) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          text = "Passwort ändern"
        )

        // Change e-mail button
        ActionButton(
          onClick = { openSheet(SettingsType.EMAIL) },
          modifier = Modifier
            .fillMaxWidth(),
          text = "E-Mail ändern"
        )

        SectionHeader("Tagesabschluss")

        // Change end of day time button
        ActionButton(
          onClick = { openSheet(SettingsType.ENDOFDAY) },
          modifier = Modifier.fillMaxWidth(),
          text = "Tagesabschluss anpassen"
        )

        // Time picker dialog for "Tagesabschluss anpassen"
        if (showEndOfDayTimePicker) {
          DialTimePicker(
            timePickerState = endOfDayTimePickerState,
            title = "Abschlusszeit",
            onDismiss = { showEndOfDayTimePicker = false },
            onConfirm = {
              // Build a LocalDateTime
              val today = LocalDate.now()
              val localDateTime = LocalDateTime.of(
                today.year,
                today.month,
                today.dayOfMonth,
                endOfDayTimePickerState.hour,
                endOfDayTimePickerState.minute
              )

              val instant = localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()

              // Store as Timestamp
              selectedEndOfDayTime = Timestamp(Date.from(instant))

              showEndOfDayTimePicker = false
            }
          )
        }


        SectionHeader("Benachrichtigung")

        // Change notifications button
        ActionButton(
          onClick = { openSheet(SettingsType.NOTIFICATION) },
          modifier = Modifier.fillMaxWidth(),
          text = "Benachrichtigungen anpassen"
        )

        // Time picker dialog for "Tagesplan Benachrichtigung"
        if (showReminderTimePicker) {
          DialTimePicker(
            timePickerState = reminderTimePickerState,
            title = "Tagesplan",
            onDismiss = { showReminderTimePicker = false },
            onConfirm = {
              val hour = reminderTimePickerState.hour
              val minute = reminderTimePickerState.minute
              selectedReminderMinutes = hour * 60 + minute

              showReminderTimePicker = false
            }
          )
        }

        // Logout button
        TextButton(
          onClick = { authViewModel.logout(onLoggedOut) },
          modifier = Modifier.padding(vertical = 24.dp)
        ) {
          Text(
            text = "Logout",
            style = MaterialTheme.typography.bodyMedium,
            color = PriorityRed
          )
        }

        // Bottom sheet
        currentType?.let { settingsType ->
          val type = settingsContentMap.getValue(settingsType)

          // Choose content of bottom sheet depending on settings type
          BottomSheet(
            sheetState = sheetState,
            onSave = {
              when (settingsType) {
                SettingsType.USERNAME -> {
                  val newName = usernameState.text.toString().trim()
                  if (newName.isNotEmpty()) {
                    authViewModel.updateUsername(newName) {
                      Toast.makeText(context, "Nutzername aktualisiert", Toast.LENGTH_SHORT).show()
                      closeSheet()
                    }
                  } else {
                    Toast.makeText(context, "Nutzername ungültig", Toast.LENGTH_SHORT).show()
                  }
                }

                SettingsType.EMAIL -> {
                  val newEmail = newEmailState.text.toString().trim()
                  val confirmEmail = newEmailConfirmState.text.toString().trim()

                  if (newEmail.isEmpty() || confirmEmail.isEmpty()) {
                    Toast.makeText(context, "Bitte E-Mail zweimal eingeben", Toast.LENGTH_SHORT).show()
                  } else if (newEmail != confirmEmail) {
                    Toast.makeText(context, "Die E-Mails stimmen nicht überein", Toast.LENGTH_SHORT).show()
                  } else {
                    authViewModel.updateEmail(newEmail) {
                      Toast.makeText(
                        context,
                        "Bestätigungs-E-Mail wurde gesendet",
                        Toast.LENGTH_LONG
                      ).show()
                      closeSheet()
                    }
                  }
                }

                SettingsType.PASSWORD -> {
                  val newPassword = newPasswordState.text.toString()
                  val confirmPassword = newPasswordConfirmState.text.toString()

                  if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(context, "Bitte Passwort zweimal eingeben", Toast.LENGTH_SHORT).show()
                  } else if (newPassword != confirmPassword) {
                    Toast.makeText(context, "Die Passwörter stimmen nicht überein", Toast.LENGTH_SHORT).show()
                  } else {
                    authViewModel.updatePassword(newPassword) {
                      Toast.makeText(context, "Passwort aktualisiert", Toast.LENGTH_SHORT).show()
                      closeSheet()
                    }
                  }
                }

                SettingsType.PROFILE_PICTURE -> {}

                SettingsType.ENDOFDAY -> {
                  if (selectedEndOfDayTime == null) {
                    Toast.makeText(context, "Bitte eine Zeit auswählen", Toast.LENGTH_SHORT).show()
                  } else {
                    authViewModel.updateEndOfDay(selectedEndOfDayTime!!)
                    Toast.makeText(context, "Tagesabschluss aktualisiert", Toast.LENGTH_SHORT).show()
                    closeSheet()
                  }
                }

                SettingsType.NOTIFICATION -> {
                  authViewModel.updateCreateListReminder(selectedReminderMinutes)
                  DailyReminderScheduler.scheduleDailyReminder(context, selectedReminderMinutes)

                  Toast.makeText(context, "Benachrichtigung aktualisiert", Toast.LENGTH_SHORT).show()
                  closeSheet()
                }
              }
            },
            onCancel = { closeSheet() },
            title = type.title,
            description = type.description,
            content = { type.content() }
          )
        }
      }
    }
  }
}
