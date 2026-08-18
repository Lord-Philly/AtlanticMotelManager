package com.atlantic.motel.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepCrimson,
    onPrimary = Color.White,
    primaryContainer = DeepBurgundy,
    onPrimaryContainer = TextPrimary,
    secondary = Burgundy,
    onSecondary = Color.White,
    secondaryContainer = ElevatedSurface,
    onSecondaryContainer = TextPrimary,
    tertiary = Champagne,
    onTertiary = DeepBlack,
    tertiaryContainer = Color(0xFF1A1510),
    onTertiaryContainer = ChampagneLight,
    background = DeepBlack,
    onBackground = TextPrimary,
    surface = SurfaceBlack,
    onSurface = TextPrimary,
    surfaceVariant = ElevatedSurface,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    outlineVariant = Color(0xFF3D2A2F),
    error = MetallicRed,
    onError = Color.White,
    errorContainer = Color(0xFF3D1118),
    onErrorContainer = MetallicRedLight,
    inverseSurface = TextPrimary,
    inverseOnSurface = DeepBlack,
    inversePrimary = DeepBurgundy,
    surfaceTint = DeepCrimson
)

@Composable
fun MotelManagerTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepBlack.toArgb()
            window.navigationBarColor = DeepBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
