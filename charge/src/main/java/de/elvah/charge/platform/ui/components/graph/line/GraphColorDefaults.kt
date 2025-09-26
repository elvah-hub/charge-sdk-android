package de.elvah.charge.platform.ui.components.graph.line

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.elvah.charge.platform.ui.theme.brand

/**
 * Default graph colors following Jetpack Compose patterns.
 * Uses brand color for offers and gray for regular pricing.
 */
object GraphColorDefaults {

    @Composable
    fun colors(
        offerSelectedLine: Color = MaterialTheme.colorScheme.brand,
        offerSelectedArea: Color = MaterialTheme.colorScheme.brand.copy(alpha = 0.6f),
        offerUnselectedLine: Color = MaterialTheme.colorScheme.brand.copy(alpha = 0.6f),
        offerUnselectedArea: Color = MaterialTheme.colorScheme.brand.copy(alpha = 0.3f),
        regularSelectedLine: Color = Color.Companion.Gray.copy(alpha = 0.8f),
        regularSelectedArea: Color = Color.Companion.Gray.copy(alpha = 0.6f),
        regularUnselectedLine: Color = Color.Companion.Gray.copy(alpha = 0.4f),
        regularUnselectedArea: Color = Color.Companion.Gray.copy(alpha = 0.3f),
        verticalLine: Color = Color.Companion.Gray.copy(alpha = 0.8f),
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
