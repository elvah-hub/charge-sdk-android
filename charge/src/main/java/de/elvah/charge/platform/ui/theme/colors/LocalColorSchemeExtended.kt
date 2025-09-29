package de.elvah.charge.platform.ui.theme.colors

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalColorSchemeExtended = staticCompositionLocalOf {
    ColorSchemeExtended(
        primary = Color.Unspecified,
        decorativeStroke = Color.Unspecified,
        brand = Color.Unspecified,
        onBrand = Color.Unspecified,
        success = Color.Unspecified,
        onSuccess = Color.Unspecified,
    )
}
