package com.example.gamifiziertertagesplaner.ui.theme

import com.example.gamifiziertertagesplaner.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

// Create a GoogleFont provider
val provider = GoogleFont.Provider(
  providerAuthority = "com.google.android.gms.fonts",
  providerPackage = "com.google.android.gms",
  certificates = R.array.com_google_android_gms_fonts_certs
)

val interFont = GoogleFont("Inter")

val inter = FontFamily(
  Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Normal),
  Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Medium),
  Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
  bodyLarge = TextStyle(
    fontFamily = inter,
    fontWeight = FontWeight.Normal,
    fontSize = 23.sp,
  )
)