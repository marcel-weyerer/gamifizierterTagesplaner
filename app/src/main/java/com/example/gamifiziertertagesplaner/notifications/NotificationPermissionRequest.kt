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

/**
 * Composable used to ask the user for permission to send notifications
 */
@Composable
fun NotificationPermissionRequest() {
  val context = LocalContext.current

  // Permission is not needed before Android 13
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
    return

  val permission = Manifest.permission.POST_NOTIFICATIONS

  // Check if app already has permission
  val hasPermission = ContextCompat.checkSelfPermission(
    context,
    permission
  ) == PackageManager.PERMISSION_GRANTED

  if (hasPermission)
    return

  // Request permission
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) {  }

  LaunchedEffect(Unit) {
    launcher.launch(permission)
  }
}
