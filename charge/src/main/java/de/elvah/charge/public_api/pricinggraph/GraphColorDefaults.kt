package de.elvah.charge.public_api.pricinggraph

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended

/**
 * Default graph colors following Jetpack Compose patterns.
 * Uses brand color for offers and gray for regular pricing.
 */
public object GraphColorDefaults {

    @Composable
    public fun colors(
        offerSelectedLine: Color = MaterialTheme.colorSchemeExtended.brand,
        offerSelectedArea: Color = MaterialTheme.colorSchemeExtended.brand.copy(alpha = 0.6f),
        offerUnselectedLine: Color = MaterialTheme.colorSchemeExtended.brand.copy(alpha = 0.6f),
        offerUnselectedArea: Color = MaterialTheme.colorSchemeExtended.brand.copy(alpha = 0.3f),
        regularSelectedLine: Color = Color.Gray.copy(alpha = 0.8f),
        regularSelectedArea: Color = Color.Gray.copy(alpha = 0.6f),
        regularUnselectedLine: Color = Color.Gray.copy(alpha = 0.4f),
        regularUnselectedArea: Color = Color.Gray.copy(alpha = 0.3f),
        verticalLine: Color = Color.Gray.copy(alpha = 0.8f),
        currentTimeMarker: Color = MaterialTheme.colorScheme.primary
    ): GraphColors = GraphColors(
        offerSelectedLine = offerSelectedLine,
        offerSelectedArea = offerSelectedArea,
        offerUnselectedLine = offerUnselectedLine,
        offerUnselectedArea = offerUnselectedArea,
        regularSelectedLine = regularSelectedLine,
        regularSelectedArea = regularSelectedArea,
        regularUnselectedLine = regularUnselectedLine,
        regularUnselectedArea = regularUnselectedArea,
        verticalLine = verticalLine,
        currentTimeMarker = currentTimeMarker
    )
}
