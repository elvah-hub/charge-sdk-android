package de.elvah.charge.features.sites.ui.pricinggraph.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI
import de.elvah.charge.features.sites.ui.pricinggraph.mapper.toChartData
import de.elvah.charge.platform.ui.components.EnergyPriceLineChart

@Composable
internal fun PricingGraphContent(
    scheduledPricing: ScheduledPricingUI,
    modifier: Modifier = Modifier,
    minYAxisPrice: Double? = null,
    gridLineDotSize: Float = 4f
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Pricing Schedule",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Energy price line chart displaying the API data
            EnergyPriceLineChart(
                dailyData = scheduledPricing.toChartData(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                animated = true,
                showVerticalGridLines = true,
                gridLineInterval = 6,
                minuteResolution = 30,
                minYAxisPrice = minYAxisPrice,
                gridLineDotSize = gridLineDotSize
            )
            
            // Standard price info
            StandardPriceInfo(
                standardPrice = scheduledPricing.standardPrice,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun PricingGraphLoading(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
internal fun PricingGraphError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Error loading pricing data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
internal fun PricingGraphEmpty(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No pricing data available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun StandardPriceInfo(
    standardPrice: ScheduledPricingUI.PriceUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Standard Price",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "${standardPrice.energyPricePerKWh} ${standardPrice.currency}/kWh",
            style = MaterialTheme.typography.bodyMedium
        )
        standardPrice.baseFee?.let { baseFee ->
            Text(
                text = "Base fee: $baseFee ${standardPrice.currency}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}