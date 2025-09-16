package de.elvah.charge.platform.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class EnergyPriceData(
    val hour: Int,
    val price: Double,
    val currency: String = "€"
)

data class DailyEnergyData(
    val date: LocalDate,
    val hourlyData: List<EnergyPriceData>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnergyPriceChart(
    dailyData: List<DailyEnergyData>,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    showVerticalGridLines: Boolean = true,
    gridLineInterval: Int = 4
) {
    if (dailyData.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 1, // Start with today (middle page)
        pageCount = { dailyData.size }
    )
    
    val allHourlyData = dailyData.flatMap { it.hourlyData }
    val maxPrice = allHourlyData.maxOf { it.price }
    val minPrice = allHourlyData.minOf { it.price }

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
                DayChart(
                    dayData = dailyData[pageIndex],
                    maxPrice = maxPrice,
                    minPrice = minPrice,
                    progress = animatedProgress,
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval
                )
            }
        }
    }
}

@Composable
private fun DayChart(
    dayData: DailyEnergyData,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
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
        
        // Hourly chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            // Grid lines and bars
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawGridLines(
                    hourCount = dayData.hourlyData.size,
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval
                )
            }
            
            // Bars
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                dayData.hourlyData.forEach { priceData ->
                    EnergyPriceBar(
                        data = priceData,
                        maxPrice = maxPrice,
                        minPrice = minPrice,
                        progress = progress,
                        showHourLabel = priceData.hour % gridLineInterval == 0,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnergyPriceBar(
    data: EnergyPriceData,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
    showHourLabel: Boolean = true,
    barColor: Color = MaterialTheme.colorScheme.brand,
    modifier: Modifier = Modifier
) {
    val barHeight = 120.dp

    val normalizedHeight = if (maxPrice > minPrice) {
        ((data.price - minPrice) / (maxPrice - minPrice)).toFloat()
    } else {
        1f
    }

    val currentHeight = (normalizedHeight * progress).coerceIn(0f, 1f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(currentHeight)
                    .background(barColor)
                    .align(Alignment.BottomCenter)
            )
        }

        if (showHourLabel) {
            Text(
                text = "${data.hour}:00",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(18.dp)) // Reserve space for consistent layout
        }
    }
}

private fun DrawScope.drawGridLines(
    hourCount: Int,
    showVerticalGridLines: Boolean,
    gridLineInterval: Int
) {
    if (!showVerticalGridLines) return
    
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val stepWidth = size.width / hourCount
    
    // Draw vertical grid lines every gridLineInterval hours
    for (hour in 0 until hourCount step gridLineInterval) {
        val x = hour * stepWidth
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EnergyPriceChart_Preview() {
    ElvahChargeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnergyPriceChart(
                dailyData = generateThreeDaySampleData(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "High Price Variation")
@Composable
private fun EnergyPriceChartHighVariationPreview() {
    ElvahChargeTheme {
        EnergyPriceChart(
            dailyData = generateThreeDayHighVariationData(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Low Price Variation")
@Composable
private fun EnergyPriceChartLowVariationPreview() {
    ElvahChargeTheme {
        EnergyPriceChart(
            dailyData = generateThreeDayLowVariationData(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

private fun generateThreeDaySampleData(): List<DailyEnergyData> {
    val today = LocalDate.now()
    return listOf(
        DailyEnergyData(today.minusDays(1), generateSampleEnergyData()), // Yesterday
        DailyEnergyData(today, generateSampleEnergyData()), // Today
        DailyEnergyData(today.plusDays(1), generateSampleEnergyData()) // Tomorrow
    )
}

private fun generateThreeDayHighVariationData(): List<DailyEnergyData> {
    val today = LocalDate.now()
    return listOf(
        DailyEnergyData(today.minusDays(1), generateHighVariationEnergyData()), // Yesterday
        DailyEnergyData(today, generateHighVariationEnergyData()), // Today
        DailyEnergyData(today.plusDays(1), generateHighVariationEnergyData()) // Tomorrow
    )
}

private fun generateThreeDayLowVariationData(): List<DailyEnergyData> {
    val today = LocalDate.now()
    return listOf(
        DailyEnergyData(today.minusDays(1), generateLowVariationEnergyData()), // Yesterday
        DailyEnergyData(today, generateLowVariationEnergyData()), // Today
        DailyEnergyData(today.plusDays(1), generateLowVariationEnergyData()) // Tomorrow
    )
}

private fun generateSampleEnergyData(): List<EnergyPriceData> {
    return listOf(
        EnergyPriceData(0, 0.12),
        EnergyPriceData(1, 0.10),
        EnergyPriceData(2, 0.08),
        EnergyPriceData(3, 0.07),
        EnergyPriceData(4, 0.06),
        EnergyPriceData(5, 0.08),
        EnergyPriceData(6, 0.15),
        EnergyPriceData(7, 0.22),
        EnergyPriceData(8, 0.28),
        EnergyPriceData(9, 0.25),
        EnergyPriceData(10, 0.23),
        EnergyPriceData(11, 0.21),
        EnergyPriceData(12, 0.20),
        EnergyPriceData(13, 0.19),
        EnergyPriceData(14, 0.18),
        EnergyPriceData(15, 0.17),
        EnergyPriceData(16, 0.16),
        EnergyPriceData(17, 0.20),
        EnergyPriceData(18, 0.25),
        EnergyPriceData(19, 0.30),
        EnergyPriceData(20, 0.28),
        EnergyPriceData(21, 0.24),
        EnergyPriceData(22, 0.18),
        EnergyPriceData(23, 0.14)
    )
}

private fun generateHighVariationEnergyData(): List<EnergyPriceData> {
    return listOf(
        EnergyPriceData(0, 0.05),
        EnergyPriceData(1, 0.45),
        EnergyPriceData(2, 0.10),
        EnergyPriceData(3, 0.35),
        EnergyPriceData(4, 0.08),
        EnergyPriceData(5, 0.40),
        EnergyPriceData(6, 0.15),
        EnergyPriceData(7, 0.50),
        EnergyPriceData(8, 0.12),
        EnergyPriceData(9, 0.38),
        EnergyPriceData(10, 0.18),
        EnergyPriceData(11, 0.42),
        EnergyPriceData(12, 0.20),
        EnergyPriceData(13, 0.55),
        EnergyPriceData(14, 0.25),
        EnergyPriceData(15, 0.60),
        EnergyPriceData(16, 0.28),
        EnergyPriceData(17, 0.52),
        EnergyPriceData(18, 0.30),
        EnergyPriceData(19, 0.65),
        EnergyPriceData(20, 0.35),
        EnergyPriceData(21, 0.58),
        EnergyPriceData(22, 0.40),
        EnergyPriceData(23, 0.62)
    )
}

private fun generateLowVariationEnergyData(): List<EnergyPriceData> {
    return (0..23).map { hour ->
        EnergyPriceData(hour, 0.20 + (kotlin.math.sin(hour * 0.5) * 0.02))
    }
}
