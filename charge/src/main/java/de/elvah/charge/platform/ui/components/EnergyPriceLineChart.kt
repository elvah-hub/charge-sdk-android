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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val discountedPrice: Double
)

data class DailyPricingData(
    val date: LocalDate,
    val regularPrice: Double,
    val offers: List<PriceOffer> = emptyList(),
    val currency: String = "€"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnergyPriceLineChart(
    dailyData: List<DailyPricingData>,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    showVerticalGridLines: Boolean = true,
    gridLineInterval: Int = 4,
    minuteResolution: Int = 15 // Data point every N minutes
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
    val minPrice = allPrices.minOf { it }

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
                    modifier = Modifier,
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval
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
    modifier: Modifier = Modifier,
    showVerticalGridLines: Boolean = true,
    gridLineInterval: Int = 4
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
            val brandColor = MaterialTheme.colorScheme.brand
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw grid lines
                drawGridLines(
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval
                )
                
                // Draw step line chart with filled areas
                drawStepLineChart(
                    dayData = dayData,
                    maxPrice = maxPrice,
                    minPrice = minPrice,
                    progress = progress,
                    minuteResolution = minuteResolution,
                    brandColor = brandColor
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
    gridLineInterval: Int
) {
    if (!showVerticalGridLines) return
    
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val hoursInDay = 24
    val stepWidth = size.width / hoursInDay
    
    // Draw vertical grid lines every gridLineInterval hours
    for (hour in 0..hoursInDay step gridLineInterval) {
        val x = hour * stepWidth
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawStepLineChart(
    dayData: DailyPricingData,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
    minuteResolution: Int,
    brandColor: Color
) {
    val priceRange = maxPrice - minPrice
    if (priceRange <= 0) return
    
    val minutesInDay = 24 * 60
    val dataPoints = minutesInDay / minuteResolution
    val stepWidth = size.width / dataPoints
    val chartHeight = size.height * 0.9f // Leave some margin
    val chartBottom = size.height * 0.95f
    
    // Colors
    val regularColor = Color.Gray.copy(alpha = 0.6f)
    val offerColor = Color.Green.copy(alpha = 0.6f)
    val lineColor = brandColor
    
    val linePath = Path()
    val regularFillPath = Path()
    val offerFillPath = Path()
    
    var isFirstPoint = true
    var currentFillPath = regularFillPath
    var lastWasOffer = false
    var lastY = 0f
    
    for (i in 0 until (dataPoints * progress).toInt()) {
        val minute = i * minuteResolution
        val hour = minute / 60
        val minuteOfHour = minute % 60
        val currentTime = LocalTime.of(hour, minuteOfHour)
        
        // Determine current price (regular or offer)
        val (currentPrice, isOffer) = getPriceAtTime(dayData, currentTime)
        
        val x = i * stepWidth
        val normalizedPrice = ((currentPrice - minPrice) / priceRange).toFloat()
        val y = chartBottom - (normalizedPrice * chartHeight)
        
        if (isFirstPoint) {
            linePath.moveTo(x, y)
            // Start fill paths from bottom
            regularFillPath.moveTo(x, chartBottom)
            offerFillPath.moveTo(x, chartBottom)
            currentFillPath = if (isOffer) offerFillPath else regularFillPath
            currentFillPath.lineTo(x, y)
            isFirstPoint = false
            lastWasOffer = isOffer
            lastY = y
        } else {
            // Step line: horizontal then vertical
            linePath.lineTo(x, lastY) // Horizontal
            linePath.lineTo(x, y) // Vertical
            
            // Continue current fill path with horizontal step
            currentFillPath.lineTo(x, lastY) // Horizontal
            
            // Handle fill area transitions
            if (isOffer != lastWasOffer) {
                // Close current fill path at transition point
                currentFillPath.lineTo(x, chartBottom)
                
                // Start new fill path from transition point
                currentFillPath = if (isOffer) offerFillPath else regularFillPath
                currentFillPath.moveTo(x, chartBottom)
                currentFillPath.lineTo(x, lastY)
            }
            
            // Continue fill path with vertical step
            currentFillPath.lineTo(x, y) // Vertical
            
            lastWasOffer = isOffer
            lastY = y
        }
    }
    
    // Close fill paths
    val lastX = ((dataPoints * progress).toInt() - 1) * stepWidth
    regularFillPath.lineTo(lastX, chartBottom)
    offerFillPath.lineTo(lastX, chartBottom)
    regularFillPath.close()
    offerFillPath.close()
    
    // Draw filled areas
    drawPath(regularFillPath, regularColor)
    drawPath(offerFillPath, offerColor)
    
    // Draw step line
    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(width = 2.dp.toPx())
    )
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

// Sample data generation functions
private fun generateThreeDayPricingData(): List<DailyPricingData> {
    val today = LocalDate.now()
    return listOf(
        DailyPricingData(
            date = today.minusDays(1),
            regularPrice = 0.25,
            offers = listOf(
                PriceOffer(
                    TimeRange(LocalTime.of(2, 0), LocalTime.of(6, 0)),
                    0.15
                ),
                PriceOffer(
                    TimeRange(LocalTime.of(14, 30), LocalTime.of(16, 0)),
                    0.18
                )
            )
        ),
        DailyPricingData(
            date = today,
            regularPrice = 0.28,
            offers = listOf(
                PriceOffer(
                    TimeRange(LocalTime.of(1, 0), LocalTime.of(5, 0)),
                    0.12
                ),
                PriceOffer(
                    TimeRange(LocalTime.of(13, 0), LocalTime.of(15, 30)),
                    0.20
                )
            )
        ),
        DailyPricingData(
            date = today.plusDays(1),
            regularPrice = 0.26,
            offers = listOf(
                PriceOffer(
                    TimeRange(LocalTime.of(3, 30), LocalTime.of(7, 0)),
                    0.16
                ),
                PriceOffer(
                    TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 30)),
                    0.19
                )
            )
        )
    )
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
                dailyData = generateThreeDayPricingData(),
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
            dailyData = generateThreeDayPricingData(),
            minuteResolution = 5, // Every 5 minutes for smoother line
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
