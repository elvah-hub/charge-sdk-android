package de.elvah.charge.platform.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand

private data class EnergyPriceData1(
    val hour: Int,
    val price: Double,
    val currency: String = "€"
)

@Composable
private fun EnergyPriceChart1(
    data: List<EnergyPriceData>,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    if (data.isEmpty()) return
    
    val scrollState = rememberScrollState()
    val maxPrice = data.maxOf { it.price }
    val minPrice = data.minOf { it.price }
    val priceRange = maxPrice - minPrice
    
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
            Text(
                text = "Energy Price by Hours",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(vertical = 8.dp)
            ) {
                data.forEach { priceData ->
                    EnergyPriceBar(
                        data = priceData,
                        maxPrice = maxPrice,
                        minPrice = minPrice,
                        progress = animatedProgress,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Min: ${String.format("%.2f", minPrice)}€",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Max: ${String.format("%.2f", maxPrice)}€",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
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
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val barHeight = 120.dp
    val barWidth = 32.dp
    
    val normalizedHeight = if (maxPrice > minPrice) {
        ((data.price - minPrice) / (maxPrice - minPrice)).toFloat()
    } else {
        1f
    }
    
    val currentHeight = (normalizedHeight * progress).coerceIn(0f, 1f)
    
    val barColor = when {
        data.price < (minPrice + (maxPrice - minPrice) * 0.33) -> Color(0xFF4CAF50) // Green for low prices
        data.price < (minPrice + (maxPrice - minPrice) * 0.66) -> Color(0xFFFF9800) // Orange for medium prices
        else -> Color(0xFFF44336) // Red for high prices
    }
    
    Column(
        modifier = modifier.width(barWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format("%.2f", data.price),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(currentHeight)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    .background(barColor)
                    .align(Alignment.BottomCenter)
            )
        }
        
        Text(
            text = "${data.hour}:00",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun EnergyPriceLineChart1(
    data: List<EnergyPriceData>,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    if (data.isEmpty()) return
    
    val scrollState = rememberScrollState()
    val maxPrice = data.maxOf { it.price }
    val minPrice = data.minOf { it.price }
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) 1f else 1f,
        animationSpec = tween(durationMillis = 1200),
        label = "line_chart_animation"
    )
    
    val brandColor = MaterialTheme.colorScheme.brand
    
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
            Text(
                text = "Energy Price Trend",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(
                modifier = Modifier
                    .height(160.dp)
                    .horizontalScroll(scrollState)
            ) {
                Canvas(
                    modifier = Modifier.size(
                        width = (data.size * 40).dp,
                        height = 160.dp
                    )
                ) {
                    drawEnergyPriceLine(
                        data = data,
                        maxPrice = maxPrice,
                        minPrice = minPrice,
                        progress = animatedProgress,
                        brandColor = brandColor
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(top = 8.dp)
            ) {
                data.forEach { priceData ->
                    Text(
                        text = "${priceData.hour}:00",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(40.dp)
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawEnergyPriceLine(
    data: List<EnergyPriceData>,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
    brandColor: Color
) {
    if (data.size < 2) return
    
    val stepX = size.width / (data.size - 1)
    val priceRange = maxPrice - minPrice
    
    val path = Path()
    val gradientPath = Path()
    
    val visibleDataCount = (data.size * progress).toInt().coerceAtLeast(1)
    val visibleData = data.take(visibleDataCount)
    
    visibleData.forEachIndexed { index, priceData ->
        val x = index * stepX
        val normalizedPrice = if (priceRange > 0) {
            ((priceData.price - minPrice) / priceRange).toFloat()
        } else {
            0.5f
        }
        val y = size.height - (normalizedPrice * size.height * 0.8f) - size.height * 0.1f
        
        if (index == 0) {
            path.moveTo(x, y)
            gradientPath.moveTo(x, size.height)
            gradientPath.lineTo(x, y)
        } else {
            path.lineTo(x, y)
            gradientPath.lineTo(x, y)
        }
        
        drawCircle(
            color = brandColor,
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
    }
    
    gradientPath.lineTo(visibleData.lastIndex * stepX, size.height)
    gradientPath.close()
    
    drawPath(
        path = gradientPath,
        color = brandColor.copy(alpha = 0.2f)
    )
    
    drawPath(
        path = path,
        color = brandColor,
        style = Stroke(width = 3.dp.toPx())
    )
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
                data = generateSampleEnergyData(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
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
            EnergyPriceLineChart1(
                data = generateSampleEnergyData(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
private fun EnergyPriceChartDarkPreview() {
    ElvahChargeTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnergyPriceChart(
                data = generateSampleEnergyData(),
                modifier = Modifier.fillMaxWidth()
            )
            
            EnergyPriceLineChart1(
                data = generateSampleEnergyData(),
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
            data = generateHighVariationEnergyData(),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Low Price Variation")
@Composable
private fun EnergyPriceChartLowVariationPreview() {
    ElvahChargeTheme {
        EnergyPriceChart(
            data = generateLowVariationEnergyData(),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
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
    return listOf(
        EnergyPriceData(0, 0.20),
        EnergyPriceData(1, 0.21),
        EnergyPriceData(2, 0.19),
        EnergyPriceData(3, 0.22),
        EnergyPriceData(4, 0.20),
        EnergyPriceData(5, 0.21),
        EnergyPriceData(6, 0.23),
        EnergyPriceData(7, 0.22),
        EnergyPriceData(8, 0.20),
        EnergyPriceData(9, 0.21),
        EnergyPriceData(10, 0.19),
        EnergyPriceData(11, 0.22)
    )
}
