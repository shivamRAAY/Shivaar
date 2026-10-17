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

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = OnIndigoContainer,
    secondary = IndigoPrimaryLight,
    onSecondary = Color.White,
    tertiary = SuccessEmerald,
    onTertiary = Color.White,
    background = SurfaceWarmWhite,
    onBackground = CharcoalTextPrimary,
    surface = SurfaceCard,
    onSurface = CharcoalTextPrimary,
    surfaceVariant = SurfaceCardSubtle,
    onSurfaceVariant = CharcoalTextSecondary,
    outline = SurfaceCardBorder,
    error = DangerRose,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = DarkBackground,
    primaryContainer = Color(0xFF1E1B4B),
    onPrimaryContainer = Color(0xFFC7D2FE),
    secondary = Color(0xFF818CF8),
    onSecondary = DarkBackground,
    tertiary = SuccessEmerald,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceBorder,
    error = DangerRose,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our tailored Shivaar brand colors by default
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
