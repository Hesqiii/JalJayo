package id.jaljayo.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val NightColorScheme = darkColorScheme(
    primary = NightPrimary,
    onPrimary = NightOnPrimary,
    secondary = NightSecondary,
    onSecondary = NightOnSecondary,
    background = NightBackground,
    onBackground = NightOnBackground,
    surface = NightSurface,
    onSurface = NightOnBackground,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightOnSurfaceVariant
)

private val DayColorScheme = lightColorScheme(
    primary = DayPrimary,
    onPrimary = DayOnPrimary,
    background = DayBackground,
    onBackground = DayOnBackground,
    surface = DaySurface,
    onSurface = DayOnBackground,
    surfaceVariant = DaySurfaceVariant,
    onSurfaceVariant = DayOnSurfaceVariant
)

@Composable
fun JalJayoTheme(
    isNightMode: Boolean = true,
    content: @Composable () -> Unit
) {
    // Mode malam selalu menggunakan NightColorScheme terlepas dari dynamic color
    // untuk menjamin proteksi emisi cahaya biru saat di kasur.
    val colorScheme = if (isNightMode) NightColorScheme else DayColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}