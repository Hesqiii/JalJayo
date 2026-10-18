package id.jaljayo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import id.jaljayo.core.Mode

private val NightColorScheme = darkColorScheme(
    primary = NightPrimary,
    onPrimary = NightOnPrimary,
    secondary = NightAccent,
    onSecondary = NightOnAccent,
    secondaryContainer = NightSurface,
    onSecondaryContainer = NightOnBackground,
    background = NightBackground,
    onBackground = NightOnBackground,
    surface = NightSurface,
    onSurface = NightOnBackground,
    surfaceVariant = NightSurface,
    onSurfaceVariant = NightTextSecondary,
    outline = NightOutline,
    error = NightError
)

private val DayColorScheme = lightColorScheme(
    primary = DayPrimary,
    onPrimary = DayOnPrimary,
    secondary = DayAccent,
    onSecondary = DayOnAccent,
    secondaryContainer = DayAccentSoft,
    onSecondaryContainer = DayOnAccent,
    background = DayBackground,
    onBackground = DayOnBackground,
    surface = DaySurface,
    onSurface = DayOnBackground,
    surfaceVariant = DaySurface,
    onSurfaceVariant = DayTextSecondary,
    outline = DayOutline,
    error = DayError
)

@Composable
fun JalJayoTheme(
    mode: Mode,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (mode == Mode.NIGHT) NightColorScheme else DayColorScheme,
        typography = Typography,
        shapes = JalJayoShapes,
        content = content
    )
}
