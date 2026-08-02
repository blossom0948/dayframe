package com.example.dayframe.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.dayframe.data.preferences.ThemeMode

private val DayframeLightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2563EB),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFDBEAFE),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF0B2B66),
    background = androidx.compose.ui.graphics.Color(0xFFF7F8FA),
    surface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE9EDF3),
    onSurface = androidx.compose.ui.graphics.Color(0xFF17191C),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF717780),
)

private val DayframeDarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF8DB4FF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF002E6D),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF124B9C),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFD9E7FF),
    background = androidx.compose.ui.graphics.Color(0xFF101216),
    surface = androidx.compose.ui.graphics.Color(0xFF181B20),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2A2F37),
    onSurface = androidx.compose.ui.graphics.Color(0xFFF4F5F7),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFA8ADB5),
)

@Composable
fun DayframeTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val colors = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themeMode == ThemeMode.SYSTEM && dark ->
            dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themeMode == ThemeMode.SYSTEM ->
            dynamicLightColorScheme(context)
        dark -> DayframeDarkColors
        else -> DayframeLightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
