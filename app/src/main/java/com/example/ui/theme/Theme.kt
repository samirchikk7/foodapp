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

private val DarkColorScheme = darkColorScheme(
    primary = SleekPrimaryDark,
    onPrimary = SleekOnPrimaryDark,
    primaryContainer = SleekPrimaryContainerDark,
    onPrimaryContainer = SleekOnPrimaryContainerDark,
    background = SleekBackgroundDark,
    surface = SleekSurfaceDark,
    onBackground = SleekOnBackgroundDark,
    onSurface = SleekOnSurfaceDark,
    outline = SleekBorderMedium,
    outlineVariant = SleekBorderLight
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPrimary,
    onPrimary = SleekOnPrimary,
    primaryContainer = SleekPrimaryContainer,
    onPrimaryContainer = SleekOnPrimaryContainer,
    background = SleekBackground,
    surface = SleekSurface,
    onBackground = SleekOnBackground,
    onSurface = SleekOnSurface,
    outline = SleekBorderMedium,
    outlineVariant = SleekBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to prioritize our brand's vibrant green signature theme!
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
