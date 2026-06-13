package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val CyberColorScheme = darkColorScheme(
  primary = CyberPrimary,
  secondary = CyberSecondary,
  tertiary = CyberTertiary,
  background = CyberBg,
  surface = CyberSurface,
  onPrimary = CyberTextOnPrimary,
  onSecondary = CyberTextOnPrimary,
  onTertiary = CyberTextOnPrimary,
  onBackground = CyberTextPrimary,
  onSurface = CyberTextPrimary,
  error = CyberError
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CyberColorScheme,
    typography = Typography,
    content = content
  )
}
