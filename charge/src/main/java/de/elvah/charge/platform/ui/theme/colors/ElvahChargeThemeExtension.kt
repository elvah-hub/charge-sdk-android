package de.elvah.charge.platform.ui.theme.colors

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

// add colors as extension of our material theme
internal object ElvahChargeThemeExtension {

    @Suppress("UnusedReceiverParameter")
    internal val MaterialTheme.colorSchemeExtended: ColorSchemeExtended
        @Composable
        get() = LocalColorSchemeExtended.current
}
