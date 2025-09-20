package de.elvah.charge.platform.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TimeRange(
    val startTime: LocalTime, // hour:minute
    val endTime: LocalTime    // hour:minute
)

data class PriceOffer(
    val timeRange: TimeRange,
    val discountedPrice: Double,
    val isSelected: Boolean = false // Future selection state
)

data class DailyPricingData(
    val date: LocalDate,
    val regularPrice: Double,
    val offers: List<PriceOffer> = emptyList(),
    val currency: String = "€",
    val isSelected: Boolean = false // Future selection state for entire day
)

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
    val verticalLine: Color
)

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
        regularSelectedLine: Color = Color.Gray.copy(alpha = 0.8f),
        regularSelectedArea: Color = Color.Gray.copy(alpha = 0.6f),
        regularUnselectedLine: Color = Color.Gray.copy(alpha = 0.4f),
        regularUnselectedArea: Color = Color.Gray.copy(alpha = 0.3f),
        verticalLine: Color = Color.Gray.copy(alpha = 0.8f)
    ): GraphColors = GraphColors(
        offerSelectedLine = offerSelectedLine,
        offerSelectedArea = offerSelectedArea,
        offerUnselectedLine = offerUnselectedLine,
        offerUnselectedArea = offerUnselectedArea,
        regularSelectedLine = regularSelectedLine,
        regularSelectedArea = regularSelectedArea,
        regularUnselectedLine = regularUnselectedLine,
        regularUnselectedArea = regularUnselectedArea,
        verticalLine = verticalLine
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnergyPriceLineChart(
    dailyData: List<DailyPricingData>,
    modifier: Modifier = Modifier,
    colors: GraphColors = GraphColorDefaults.colors(),
    animated: Boolean = true,
    showVerticalGridLines: Boolean = true,
    gridLineInterval: Int = 4,
    minuteResolution: Int = 15, // Data point every N minutes
    minYAxisPrice: Double? = null, // User-defined minimum Y-axis price
    gridLineDotSize: Float = 4f // Size of dots in dotted grid lines
) {
    if (dailyData.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 1, // Start with today (middle page)
        pageCount = { dailyData.size }
    )

    val allPrices = dailyData.flatMap { day ->
        listOf(day.regularPrice) + day.offers.map { it.discountedPrice }
    }
    val maxPrice = allPrices.maxOf { it }
    val calculatedMinPrice = allPrices.minOf { it }
    val minPrice = minYAxisPrice?.let { userMin ->
        minOf(userMin, calculatedMinPrice)
    } ?: calculatedMinPrice

    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) 1f else 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "chart_animation"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { pageIndex ->
                DayLineChart(
                    dayData = dailyData[pageIndex],
                    maxPrice = maxPrice,
                    minPrice = minPrice,
                    progress = animatedProgress,
                    minuteResolution = minuteResolution,
                    colors = colors,
                    modifier = Modifier,
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval,
                    gridLineDotSize = gridLineDotSize
                )
            }
        }
    }
}

@Composable
private fun DayLineChart(
    dayData: DailyPricingData,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
    minuteResolution: Int,
    colors: GraphColors,
    modifier: Modifier = Modifier,
    showVerticalGridLines: Boolean = true,
    gridLineInterval: Int = 4,
    gridLineDotSize: Float = 4f
) {
    Column(modifier = modifier) {
        // Date header
        Text(
            text = dayData.date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Line chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw grid lines
                drawGridLines(
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval,
                    gridLineDotSize = gridLineDotSize
                )

                // Draw step line chart with filled areas
                drawStepLineChart(
                    dayData = dayData,
                    maxPrice = maxPrice,
                    minPrice = minPrice,
                    progress = progress,
                    minuteResolution = minuteResolution,
                    colors = colors
                )
            }
        }

        // X-axis labels (hours)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (hour in 0..23 step gridLineInterval) {
                Text(
                    text = "${hour}:00",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun DrawScope.drawGridLines(
    showVerticalGridLines: Boolean,
    gridLineInterval: Int,
    gridLineDotSize: Float = 4f
) {
    if (!showVerticalGridLines) return

    // Vertical grid lines are always gray regardless of selection state
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val hoursInDay = 24
    val stepWidth = size.width / hoursInDay

    // Create dotted path effect
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(gridLineDotSize, gridLineDotSize), 0f)

    // Draw vertical dotted grid lines every gridLineInterval hours
    for (hour in 0..hoursInDay step gridLineInterval) {
        val x = hour * stepWidth
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = pathEffect
        )
    }
}

private fun DrawScope.drawStepLineChart(
    dayData: DailyPricingData,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
    minuteResolution: Int,
    colors: GraphColors
) {
    val priceRange = maxPrice - minPrice
    if (priceRange <= 0) return

    val minutesInDay = 24 * 60
    val dataPoints = minutesInDay / minuteResolution
    val stepWidth = size.width / dataPoints
    val chartHeight = size.height * 0.9f // Leave some margin
    val chartBottom = size.height * 0.95f

    // Separate paths for different pricing types and selection states
    val regularSelectedHorizontalPath = Path()
    val regularUnselectedHorizontalPath = Path()
    val offerSelectedHorizontalPath = Path()
    val offerUnselectedHorizontalPath = Path()
    val verticalLinesPath = Path()
    
    val regularSelectedFillPath = Path()
    val regularUnselectedFillPath = Path()
    val offerSelectedFillPath = Path()
    val offerUnselectedFillPath = Path()

    var isFirstPoint = true
    var currentFillPath: Path? = null
    var lastWasOffer = false
    var lastWasSelected = false
    var lastY = 0f

    for (i in 0 until (dataPoints * progress).toInt()) {
        val minute = i * minuteResolution
        val hour = minute / 60
        val minuteOfHour = minute % 60
        val currentTime = LocalTime.of(hour, minuteOfHour)

        // Determine current price and selection state
        val (currentPrice, isOffer) = getPriceAtTime(dayData, currentTime)
        val isSelected = isTimeSlotSelected(dayData, currentTime)

        val x = i * stepWidth
        val normalizedPrice = ((currentPrice - minPrice) / priceRange).toFloat()
        val y = chartBottom - (normalizedPrice * chartHeight)

        if (isFirstPoint) {
            // Initialize all fill paths
            regularSelectedFillPath.moveTo(x, chartBottom)
            regularUnselectedFillPath.moveTo(x, chartBottom)
            offerSelectedFillPath.moveTo(x, chartBottom)
            offerUnselectedFillPath.moveTo(x, chartBottom)
            
            // Select the appropriate fill path
            currentFillPath = when {
                isOffer && isSelected -> offerSelectedFillPath
                isOffer && !isSelected -> offerUnselectedFillPath
                !isOffer && isSelected -> regularSelectedFillPath
                else -> regularUnselectedFillPath
            }
            currentFillPath?.lineTo(x, y)
            
            isFirstPoint = false
            lastWasOffer = isOffer
            lastWasSelected = isSelected
            lastY = y
        } else {
            // Add horizontal line to appropriate path
            val horizontalPath = when {
                lastWasOffer && lastWasSelected -> offerSelectedHorizontalPath
                lastWasOffer && !lastWasSelected -> offerUnselectedHorizontalPath
                !lastWasOffer && lastWasSelected -> regularSelectedHorizontalPath
                else -> regularUnselectedHorizontalPath
            }
            horizontalPath.moveTo((i - 1) * stepWidth, lastY)
            horizontalPath.lineTo(x, lastY)

            // Add vertical line (always same color) when there's a price change
            if (lastY != y) {
                verticalLinesPath.moveTo(x, lastY)
                verticalLinesPath.lineTo(x, y)
            }

            // Continue current fill path with horizontal step
            currentFillPath?.lineTo(x, lastY)

            // Handle transitions between different pricing/selection states
            val stateChanged = (isOffer != lastWasOffer) || (isSelected != lastWasSelected)
            if (stateChanged) {
                // Close current fill path at transition point
                currentFillPath?.lineTo(x, chartBottom)

                // Select new fill path
                currentFillPath = when {
                    isOffer && isSelected -> offerSelectedFillPath
                    isOffer && !isSelected -> offerUnselectedFillPath
                    !isOffer && isSelected -> regularSelectedFillPath
                    else -> regularUnselectedFillPath
                }
                currentFillPath?.moveTo(x, chartBottom)
                currentFillPath?.lineTo(x, lastY)
            }

            // Continue fill path with vertical step
            currentFillPath?.lineTo(x, y)

            lastWasOffer = isOffer
            lastWasSelected = isSelected
            lastY = y
        }
    }

    // Close all fill paths
    val lastX = ((dataPoints * progress).toInt() - 1) * stepWidth
    regularSelectedFillPath.lineTo(lastX, chartBottom)
    regularUnselectedFillPath.lineTo(lastX, chartBottom)
    offerSelectedFillPath.lineTo(lastX, chartBottom)
    offerUnselectedFillPath.lineTo(lastX, chartBottom)
    listOf(regularSelectedFillPath, regularUnselectedFillPath, offerSelectedFillPath, offerUnselectedFillPath)
        .forEach { it.close() }

    // Draw filled areas with state-specific colors
    drawPath(regularSelectedFillPath, colors.regularSelectedArea)
    drawPath(regularUnselectedFillPath, colors.regularUnselectedArea)
    drawPath(offerSelectedFillPath, colors.offerSelectedArea)
    drawPath(offerUnselectedFillPath, colors.offerUnselectedArea)

    // Draw horizontal lines with state-specific colors
    drawPath(regularSelectedHorizontalPath, colors.regularSelectedLine, style = Stroke(width = 2.dp.toPx()))
    drawPath(regularUnselectedHorizontalPath, colors.regularUnselectedLine, style = Stroke(width = 2.dp.toPx()))
    drawPath(offerSelectedHorizontalPath, colors.offerSelectedLine, style = Stroke(width = 2.dp.toPx()))
    drawPath(offerUnselectedHorizontalPath, colors.offerUnselectedLine, style = Stroke(width = 2.dp.toPx()))
    
    // Draw all vertical lines with consistent color
    drawPath(verticalLinesPath, colors.verticalLine, style = Stroke(width = 2.dp.toPx()))
}

private fun getPriceAtTime(dayData: DailyPricingData, time: LocalTime): Pair<Double, Boolean> {
    // Check if time falls within any offer period
    for (offer in dayData.offers) {
        if (time >= offer.timeRange.startTime && time < offer.timeRange.endTime) {
            return offer.discountedPrice to true
        }
    }
    // Return regular price if no offer applies
    return dayData.regularPrice to false
}

// Helper function to get offer at specific time for future selection handling
private fun getOfferAtTime(dayData: DailyPricingData, time: LocalTime): PriceOffer? {
    return dayData.offers.find { offer ->
        time >= offer.timeRange.startTime && time < offer.timeRange.endTime
    }
}

// Helper function to determine if a time slot is selected (for future use)
private fun isTimeSlotSelected(dayData: DailyPricingData, time: LocalTime): Boolean {
    val offer = getOfferAtTime(dayData, time)
    return offer?.isSelected ?: dayData.isSelected
}

@Preview(showBackground = true)
@Composable
private fun EnergyPriceLineChart_Preview() {
    ElvahChargeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnergyPriceLineChart(
                dailyData = MockData.generateThreeDayPricingData(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "High Resolution")
@Composable
private fun EnergyPriceLineChartHighResPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            minuteResolution = 5, // Every 5 minutes for smoother line
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Custom Min Y-Axis Price")
@Composable
private fun EnergyPriceLineChartCustomMinYAxisPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            minYAxisPrice = 0.0, // Set minimum Y-axis price to 0.0
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Dotted Grid Lines")
@Composable
private fun EnergyPriceLineChartDottedGridPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            gridLineDotSize = 6f, // Larger dots for demonstration
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

