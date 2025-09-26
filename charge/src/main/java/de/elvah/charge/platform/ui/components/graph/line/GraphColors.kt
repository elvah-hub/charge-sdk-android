package de.elvah.charge.platform.ui.components.graph.line

import androidx.compose.ui.graphics.Color

/**
 * Defines all color variations for the graph with full combination support:
 * - Area and Line colors
 * - Offer and Regular pricing types
 * - Selected and Unselected states
 */
data class GraphColors(
    // Offer colors - selected state
    val offerSelectedLine: Color,
    val offerSelectedArea: Color,
    // Offer colors - unselected state
    val offerUnselectedLine: Color,
    val offerUnselectedArea: Color,
    // Regular colors - selected state
    val regularSelectedLine: Color,
    val regularSelectedArea: Color,
    // Regular colors - unselected state
    val regularUnselectedLine: Color,
    val regularUnselectedArea: Color,
    // Vertical transition lines (always same color regardless of selection)
    val verticalLine: Color,
    // Current time marker
    val currentTimeMarker: Color
)
