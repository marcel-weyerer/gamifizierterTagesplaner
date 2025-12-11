package com.example.gamifiziertertagesplaner.feature.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.BottomSheet
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.EmailContent
import com.example.gamifiziertertagesplaner.components.NotificationContent
import com.example.gamifiziertertagesplaner.components.PasswordContent
import com.example.gamifiziertertagesplaner.components.ProfilePictureContent
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.components.SettingsContent
import com.example.gamifiziertertagesplaner.components.SettingsType
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.components.UsernameContent
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  authViewModel: AuthViewModel,
  onOpenHome: () -> Unit,
  onOpenPomodoro: () -> Unit,
  onOpenBookshelf: () -> Unit,
  onLoggedOut: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  val context = LocalContext.current

  var currentType by remember { mutableStateOf<SettingsType?>(null) }

  // Input states for all profile properties
  val usernameState = remember { TextFieldState() }

  val newEmailState = remember { TextFieldState() }
  val newEmailConfirmState = remember { TextFieldState() }

  val newPasswordState = remember { TextFieldState() }
  val newPasswordConfirmState = remember { TextFieldState() }

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

  fun closeSheet() {
    scope.launch {
      sheetState.hide()
    }.invokeOnCompletion {
      currentType = null
    }
  }

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
      SettingsType.NOTIFICATION to SettingsContent(
        title = "Benachrichtigungen anpassen",
        description = "Passe deine Benachrichtigungen an",
        content = { NotificationContent() }
      )
    )
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

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp)
          .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        val profileImageUrl = authViewModel.userProfile?.photoUrl
        val username = authViewModel.userProfile?.username ?: "User"

        IconButton(
          onClick = { openSheet(SettingsType.PROFILE_PICTURE) },
          modifier = Modifier.size(150.dp)
        ) {

          if (profileImageUrl != null) {
            AsyncImage(
              model = profileImageUrl,
              contentDescription = "Profile Picture",
              modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
              contentScale = ContentScale.Crop
            )
          } else {
            Icon(
              imageVector = Icons.Default.Circle,
              contentDescription = "Default Profile Icon",
              modifier = Modifier.fillMaxSize(),
              tint = MaterialTheme.colorScheme.onBackground
            )
          }
        }

        Text(
          modifier = Modifier.padding(vertical = 12.dp),
          text = username,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onBackground
        )

        SectionHeader("Nutzer Profil")

        ActionButton(
          onClick = { openSheet(SettingsType.USERNAME) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          text = "Nutzername ändern"
        )

        ActionButton(
          onClick = { openSheet(SettingsType.PASSWORD) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          text = "Passwort ändern"
        )

        ActionButton(
          onClick = { openSheet(SettingsType.EMAIL) },
          modifier = Modifier
            .fillMaxWidth(),
          text = "E-Mail ändern"
        )

        SectionHeader("Benachrichtigung")

        ActionButton(
          onClick = { openSheet(SettingsType.NOTIFICATION) },
          modifier = Modifier.fillMaxWidth(),
          text = "Benachrichtigungen anpassen"
        )

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

                SettingsType.NOTIFICATION -> {
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
