package de.elvah.charge.platform.ui.components.graph.line

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object GraphConstants {
    // Chart dimensions
    const val DEFAULT_CHART_HEIGHT_DP = 200
    const val DEFAULT_BAR_HEIGHT_DP = 120
    const val DEFAULT_BAR_WIDTH_DP = 32
    
    // Animation
    const val DEFAULT_ANIMATION_DURATION_MS = 1000
    const val LINE_CHART_ANIMATION_DURATION_MS = 1200
    
    // Grid lines
    const val DEFAULT_GRID_LINE_INTERVAL = 4
    const val DEFAULT_GRID_LINE_DOT_SIZE = 4f
    const val GRID_LINE_STROKE_WIDTH_DP = 1f
    const val GRID_LINE_ALPHA = 0.3f
    
    // Chart layout
    const val CHART_HEIGHT_MULTIPLIER = 0.8f
    const val CHART_BOTTOM_MULTIPLIER = 0.85f
    const val CHART_VERTICAL_MARGIN_MULTIPLIER = 0.1f
    const val CHART_CONTENT_MULTIPLIER = 0.8f
    
    // Line drawing
    const val LINE_STROKE_WIDTH_DP = 2f
    const val CIRCLE_MARKER_RADIUS_DP = 4f
    const val CIRCLE_MARKER_INNER_RADIUS_DP = 2f
    const val LINE_CHART_CIRCLE_RADIUS_DP = 4f
    const val LINE_CHART_STROKE_WIDTH_DP = 3f
    
    // Time calculations
    const val HOURS_IN_DAY = 24
    const val MINUTES_IN_HOUR = 60
    const val MINUTES_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR
    const val DEFAULT_MINUTE_RESOLUTION = 15
    
    // UI spacing
    val CARD_PADDING = 16.dp
    val SECTION_SPACING = 16.dp
    val SMALL_SPACING = 8.dp
    val TINY_SPACING = 4.dp
    val BADGE_PADDING_HORIZONTAL = 8.dp
    val BADGE_PADDING_VERTICAL = 10.dp
    val ICON_SIZE_SMALL = 12.dp
    val SPACER_WIDTH = 10.dp
    val SPACER_SIZE = 6.dp
    
    // Typography
    val SMALL_TEXT_SIZE = 10.sp
    val GRID_TEXT_SIZE = 10.sp
    
    // Colors
    const val BRAND_COLOR_ALPHA_60 = 0.6f
    const val BRAND_COLOR_ALPHA_30 = 0.3f
    const val GRAY_COLOR_ALPHA_80 = 0.8f
    const val GRAY_COLOR_ALPHA_60 = 0.6f
    const val GRAY_COLOR_ALPHA_40 = 0.4f
    const val GRAY_COLOR_ALPHA_30 = 0.3f
    const val SURFACE_ALPHA_70 = 0.7f
    const val GRADIENT_ALPHA = 0.2f
    
    // Offer badge colors
    const val OFFER_BACKGROUND_COLOR = 0x1A279138
    
    // Color thresholds for bar colors
    const val LOW_PRICE_THRESHOLD = 0.33
    const val MEDIUM_PRICE_THRESHOLD = 0.66
    
    // Bar colors
    const val LOW_PRICE_COLOR = 0xFF4CAF50
    const val MEDIUM_PRICE_COLOR = 0xFFFF9800
    const val HIGH_PRICE_COLOR = 0xFFF44336
    
    // Chart elevation
    val CARD_ELEVATION = 2.dp
    
    // Layout weights
    const val EQUAL_WEIGHT = 1f
    
    // Border radius
    val ROUNDED_CORNER_RADIUS = 100.dp
    val BAR_CORNER_RADIUS = 4.dp
    
    // Chart canvas sizing
    const val CANVAS_WIDTH_MULTIPLIER = 40
    const val CANVAS_HEIGHT_DP = 160
}