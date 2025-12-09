package com.example.gamifiziertertagesplaner

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.gamifiziertertagesplaner.ui.theme.GamifizierterTagesplanerTheme

class MainActivity : ComponentActivity() {
  @RequiresApi(Build.VERSION_CODES.O)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GamifizierterTagesplanerTheme {     // Use Tagesplaner theme
        App()
      }
    }
  }
}