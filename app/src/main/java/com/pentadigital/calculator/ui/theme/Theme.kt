package com.pentadigital.calculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color
import com.pentadigital.calculator.viewmodels.AccentColor
import com.pentadigital.calculator.viewmodels.AppTheme
import com.pentadigital.calculator.viewmodels.ThemeState

private val ModernDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberpunkDarkBG,
    primaryContainer = NeonPurple,
    onPrimaryContainer = Color.White,
    secondary = NeonGreen,
    onSecondary = CyberpunkDarkBG,
    secondaryContainer = CyberpunkSurface,
    onSecondaryContainer = CyberpunkTextPrimary,
    tertiary = HoloBlue,
    onTertiary = CyberpunkDarkBG,
    background = CyberpunkDarkBG,
    onBackground = CyberpunkTextPrimary,
    surface = CyberpunkSurface,
    onSurface = CyberpunkTextPrimary,
    surfaceVariant = CyberpunkSurface,
    onSurfaceVariant = CyberpunkTextSecondary,
    outline = NeonCyan.copy(alpha = 0.5f),
    error = HealthRed,
    onError = Color.White
)

private val ModernLightColorScheme = lightColorScheme(
    primary = LightTechBlue,
    onPrimary = Color.White,
    primaryContainer = LightTechPurple,
    onPrimaryContainer = Color.White,
    secondary = LightTechGreen,
    onSecondary = Color.White,
    secondaryContainer = CyberpunkLightSurface,
    onSecondaryContainer = CyberpunkLightTextPrimary,
    tertiary = LightTechBlue, // fallback
    onTertiary = Color.White,
    background = CyberpunkLightBG,
    onBackground = CyberpunkLightTextPrimary,
    surface = CyberpunkLightSurface,
    onSurface = CyberpunkLightTextPrimary,
    surfaceVariant = CyberpunkLightSurface,
    onSurfaceVariant = CyberpunkLightTextSecondary,
    outline = CyberpunkLightBorder,
    error = HealthRed,
    onError = Color.White
)

@Composable
fun CalculatorTheme(
    themeState: ThemeState = ThemeState(),
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeState.appTheme) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    } 

    val accentColor = when (themeState.accentColor) {
        AccentColor.Orange -> Orange
        AccentColor.Blue -> PrimaryBrand
        AccentColor.Green -> FinanceGreen
        AccentColor.Pink -> Pink
    }

    // Select scheme based on darkTheme
    val baseScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme
    
    val colorScheme = baseScheme.copy(
        primary = accentColor,
        secondary = accentColor
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
