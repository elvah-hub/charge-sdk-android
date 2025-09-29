package de.elvah.charge.platform.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    secondary = secondaryDark,
    background = containerDark,
    surface = canvasDark,
    tertiary = canvasDark,
    onTertiary = containerDark
)

private val LightColorScheme = lightColorScheme(
    primary = primary,
    secondary = secondary,
    background = container,
    surface = canvas,
    tertiary = container,
    onTertiary = canvas
)

@Composable
internal fun ElvahChargeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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

internal val ColorScheme.brand: Color
    get() = brandColor


internal val ColorScheme.onBrand: Color
    get() = onBrandColor

@Composable
internal fun shouldUseDarkColors(darkTheme: Boolean?): Boolean = darkTheme ?: isSystemInDarkTheme()

