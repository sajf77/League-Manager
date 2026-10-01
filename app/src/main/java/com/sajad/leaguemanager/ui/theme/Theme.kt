package com.sajad.leaguemanager.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val NoRoundedShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkPrimary,
    primaryContainer = DarkAccentDim,
    onPrimaryContainer = DarkAccent,
    secondary = DarkAccentBlue,
    onSecondary = Color.White,
    secondaryContainer = DarkAccentBlueDim,
    background = DarkPrimary,
    onBackground = DarkTextPrimary,
    surface = DarkCard,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSecondary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderFocus,
    error = ErrorRed,
    onError = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = Color.White,
    primaryContainer = LightAccentDim,
    onPrimaryContainer = LightAccent,
    secondary = LightAccentBlue,
    onSecondary = Color.White,
    secondaryContainer = LightAccentBlueDim,
    background = LightPrimary,
    onBackground = LightTextPrimary,
    surface = LightCard,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSecondary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightBorderFocus,
    error = ErrorRed,
    onError = Color.White,
)

@Composable
fun LeagueManagerTheme(
    darkTheme: Boolean = true,
    fontSize: String = "default",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typographyFor(fontSize),
        shapes = NoRoundedShapes,
        content = content
    )
}
