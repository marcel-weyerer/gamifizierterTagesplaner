package com.example.gamifiziertertagesplaner.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamifiziertertagesplaner.R
import com.example.gamifiziertertagesplaner.components.ActionButton
import com.example.gamifiziertertagesplaner.components.BottomAppBarOption
import com.example.gamifiziertertagesplaner.components.BottomSheet
import com.example.gamifiziertertagesplaner.components.CustomBottomAppBar
import com.example.gamifiziertertagesplaner.components.SectionHeader
import com.example.gamifiziertertagesplaner.components.SettingsType
import com.example.gamifiziertertagesplaner.components.TopScreenTitle
import com.example.gamifiziertertagesplaner.components.rememberSettingsContentMap
import com.example.gamifiziertertagesplaner.firestore.AuthViewModel
import com.example.gamifiziertertagesplaner.ui.theme.PriorityRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  authViewModel: AuthViewModel = viewModel(),
  onOpenHome: () -> Unit,
  onOpenPomodoro: () -> Unit,
  onOpenBookshelf: () -> Unit,
  onLoggedOut: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  val settingsContentMap = rememberSettingsContentMap()

  var currentType by remember { mutableStateOf<SettingsType?>(null) }

  fun openSheet(type: SettingsType) {
    currentType = type
    scope.launch { sheetState.show() }
  }

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

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp)
          .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        IconButton(
          onClick = { openSheet(SettingsType.PROFILE_PICTURE) },
          modifier = Modifier.size(150.dp),
        ) {
          Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = Icons.Default.Circle,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = "Profile Picture"
          )
        }

        Text(
          modifier = Modifier.padding(vertical = 12.dp),
          text = "Username",
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

        currentType?.let { settingsType ->
          val type = settingsContentMap.getValue(settingsType)

          BottomSheet(
            sheetState = sheetState,
            onSave = {
              closeSheet()
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
