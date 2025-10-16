package de.elvah.charge.platform.ui.theme.colors

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

internal object ElvahChargeColors {

    private val LightColorScheme = lightColorScheme(
        primary = primary,
        secondary = secondary,
        background = container,
        surface = canvas,
        tertiary = container,
        onTertiary = canvas,
    )

    internal val LightColorSchemeExtended = ColorSchemeExtended(
        primary = primary,
        decorativeStroke = decorativeStroke,
        brand = brandColor,
        onBrand = onBrandColor,
        success = success,
        onSuccess = onSuccess,
        error = error,
        onError = onError
    )

    private val DarkColorScheme = darkColorScheme(
        primary = primaryDark,
        secondary = secondaryDark,
        background = containerDark,
        surface = canvasDark,
        tertiary = canvasDark,
        onTertiary = containerDark,
    )

    private val DarkColorSchemeExtended = ColorSchemeExtended(
        primary = primary,
        decorativeStroke = decorativeStroke,
        brand = brandColor,
        onBrand = onBrandColor,
        success = success,
        onSuccess = onSuccess,
        error = error,
        onError = onError
    )

    @Composable
    fun getColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
        return when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }

    fun getColorSchemeExtended(darkTheme: Boolean): ColorSchemeExtended {
        return when {
            darkTheme -> DarkColorSchemeExtended
            else -> LightColorSchemeExtended
        }
    }
}
