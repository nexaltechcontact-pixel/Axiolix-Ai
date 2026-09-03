package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FuturisticDarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = CyberBlack,
  primaryContainer = Color(0xFF003840),
  onPrimaryContainer = Color(0xFFA5F3FC),
  secondary = NeonViolet,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF3B1360),
  onSecondaryContainer = Color(0xFFF3E8FF),
  tertiary = MatrixGreen,
  onTertiary = CyberBlack,
  tertiaryContainer = Color(0xFF003D24),
  onTertiaryContainer = Color(0xFFA7F3D0),
  background = CyberBlack,
  onBackground = TextPrimary,
  surface = CyberSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = CyberBorder,
  outlineVariant = CyberBorderGlow,
  error = CyberPink,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Futuristic black theme takes precedence
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = FuturisticDarkColorScheme,
    typography = Typography,
    content = content
  )
}
