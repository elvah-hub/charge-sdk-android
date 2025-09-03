package de.elvah.charge.platform.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand

data class EnergyPriceData(
    val hour: Int,
    val price: Double,
    val currency: String = "€"
)

@Composable
fun EnergyPriceChart(
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
        BoxWithConstraints(
            modifier = Modifier.padding(16.dp)
        ) {
            val barWidth = this.maxWidth / data.size
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(vertical = 8.dp)
            ) {
                data.forEach { priceData ->
                    EnergyPriceBar(
                        data = priceData,
                        barWidth = barWidth,
                        maxPrice = maxPrice,
                        minPrice = minPrice,
                        progress = animatedProgress,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun EnergyPriceBar(
    data: EnergyPriceData,
    barWidth: Dp = 32.dp,
    maxPrice: Double,
    minPrice: Double,
    progress: Float,
    barColor: Color = MaterialTheme.colorScheme.brand,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val barHeight = 120.dp

    val normalizedHeight = if (maxPrice > minPrice) {
        ((data.price - minPrice) / (maxPrice - minPrice)).toFloat()
    } else {
        1f
    }

    val currentHeight = (normalizedHeight * progress).coerceIn(0f, 1f)

    Column(
        modifier = modifier.width(barWidth),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(barWidth)
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

        Text(
            text = "${data.hour}",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
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
            data = generateLowVariationEnergyData(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
