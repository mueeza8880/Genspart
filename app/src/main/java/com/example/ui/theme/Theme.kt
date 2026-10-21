package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val KidsparkLightColorScheme = lightColorScheme(
  primary = BrandTeal,
  onPrimary = Color.White,
  primaryContainer = BrandTealContainer,
  onPrimaryContainer = BrandTealDark,
  secondary = WarmCoral,
  onSecondary = Color.White,
  secondaryContainer = WarmCoralLight,
  onSecondaryContainer = WarmCoralDark,
  tertiary = SoftYellowDark,
  onTertiary = Color.White,
  tertiaryContainer = SoftYellowLight,
  onTertiaryContainer = Color(0xFF5A4300),
  background = WarmIvoryBg,
  onBackground = DeepNavyText,
  surface = WarmSurface,
  onSurface = DeepNavyText,
  surfaceVariant = WarmSurfaceVariant,
  onSurfaceVariant = MutedSlate,
  outline = OutlineBorder,
  outlineVariant = Color(0xFFEDE3D5)
)

private val KidsparkDarkColorScheme = darkColorScheme(
  primary = Color(0xFF38B2AC),
  onPrimary = Color(0xFF00373D),
  primaryContainer = Color(0xFF065963),
  onPrimaryContainer = Color(0xFFB0F0F6),
  secondary = WarmCoral,
  onSecondary = Color(0xFF561F0F),
  secondaryContainer = Color(0xFF7A2E17),
  onSecondaryContainer = Color(0xFFFFDBCF),
  tertiary = SoftYellow,
  onTertiary = Color(0xFF423100),
  tertiaryContainer = Color(0xFF5E4700),
  onTertiaryContainer = Color(0xFFFFDF9E),
  background = DarkBg,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkOutlineBorder,
  outlineVariant = Color(0xFF263C50)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep brand personality by default
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> KidsparkDarkColorScheme
    else -> KidsparkLightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
