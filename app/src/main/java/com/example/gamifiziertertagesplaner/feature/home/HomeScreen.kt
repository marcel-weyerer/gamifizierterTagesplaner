package com.example.gamifiziertertagesplaner.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(onOpenCreateTask: () -> Unit) {
  Box(
    modifier = Modifier.fillMaxSize(),      // use the entire screen
    contentAlignment = Alignment.Center     // center content
  ) {
    Button(
      onClick = onOpenCreateTask,
      colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      )
    ) {
      Text(text = "+")
    }
  }
}