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
import com.example.data.AppThemeState

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0F172A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1F5F9),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF334155),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF8FAFC),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = Color(0xFF475569),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F172A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1F5F9),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF334155),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF8FAFC),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = Color(0xFF475569),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFCBD5E1),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
    themeState: AppThemeState = AppThemeState(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (themeState.isDark) {
        darkColorScheme(
            primary = themeState.primaryColor,
            onPrimary = Color.White,
            primaryContainer = themeState.surfaceColor,
            onPrimaryContainer = themeState.onBackgroundColor,
            secondary = themeState.primaryColor,
            onSecondary = Color.White,
            secondaryContainer = themeState.surfaceColor,
            onSecondaryContainer = themeState.onBackgroundColor,
            tertiary = themeState.onSurfaceVariant,
            background = themeState.backgroundColor,
            surface = themeState.surfaceColor,
            surfaceContainerLowest = themeState.backgroundColor,
            surfaceContainerLow = themeState.backgroundColor,
            surfaceContainer = themeState.surfaceColor,
            surfaceContainerHigh = themeState.surfaceColor,
            surfaceContainerHighest = themeState.borderColor,
            outline = themeState.borderColor,
            outlineVariant = themeState.borderColor,
            onBackground = themeState.onBackgroundColor,
            onSurface = themeState.onBackgroundColor,
            onSurfaceVariant = themeState.onSurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = themeState.primaryColor,
            onPrimary = Color.White,
            primaryContainer = themeState.surfaceColor,
            onPrimaryContainer = themeState.onBackgroundColor,
            secondary = themeState.primaryColor,
            onSecondary = Color.White,
            secondaryContainer = themeState.surfaceColor,
            onSecondaryContainer = themeState.onBackgroundColor,
            tertiary = themeState.onSurfaceVariant,
            background = themeState.backgroundColor,
            surface = themeState.surfaceColor,
            surfaceContainerLowest = themeState.backgroundColor,
            surfaceContainerLow = themeState.backgroundColor,
            surfaceContainer = themeState.surfaceColor,
            surfaceContainerHigh = themeState.surfaceColor,
            surfaceContainerHighest = themeState.borderColor,
            outline = themeState.borderColor,
            outlineVariant = themeState.borderColor,
            onBackground = themeState.onBackgroundColor,
            onSurface = themeState.onBackgroundColor,
            onSurfaceVariant = themeState.onSurfaceVariant
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
