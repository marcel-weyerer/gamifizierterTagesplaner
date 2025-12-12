package com.example.gamifiziertertagesplaner.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun NotificationPermissionRequest() {
  val context = LocalContext.current

  // Only Android 13+ needs this permission
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

  val permission = Manifest.permission.POST_NOTIFICATIONS

  // Check current permission state
  val hasPermission = ContextCompat.checkSelfPermission(
    context,
    permission
  ) == PackageManager.PERMISSION_GRANTED

  if (hasPermission) return

  // Launcher to request permission
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) {  }

  LaunchedEffect(Unit) {
    launcher.launch(permission)
  }
}
