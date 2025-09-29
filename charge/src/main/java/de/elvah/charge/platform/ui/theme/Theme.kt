package de.elvah.charge.platform.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeColors
import de.elvah.charge.platform.ui.theme.colors.LocalColorSchemeExtended
import de.elvah.charge.platform.ui.theme.colors.onBrandColor

@Composable
internal fun ElvahChargeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColorSchemeExtended provides ElvahChargeColors.getColorSchemeExtended(darkTheme),
    ) {
        MaterialTheme(
            colorScheme = ElvahChargeColors.getColorScheme(darkTheme, dynamicColor),
            typography = Typography,
            content = content
        )
    }
}

internal val ColorScheme.onBrand: Color
    get() = onBrandColor

@Composable
internal fun shouldUseDarkColors(darkTheme: Boolean?): Boolean = darkTheme ?: isSystemInDarkTheme()

