package de.elvah.charge.platform.ui.components.graph.line

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.R
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.components.graph.line.utils.getClickedTimeByOffset
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand
import java.time.LocalDate
import java.time.LocalTime

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

    // Default value is the today page and the slot based on the current hour
    val todayIndex = dailyData.indexOfFirst { it.date == LocalDate.now() }.takeIf { it != -1 } ?: 1

    val pagerState = rememberPagerState(
        initialPage = todayIndex,
        pageCount = { dailyData.size }
    )
    var selectedType by remember { mutableStateOf(ChargeType.FAST) }
    var selectedPrice by remember {
        mutableDoubleStateOf(
            getPriceAtTime(
                dailyData[todayIndex],
                LocalTime.of(LocalTime.now().hour, 0)
            ).first
        )
    }
    var offerSelected by remember { mutableStateOf(false) }
    var selectedPriceOffer by remember {
        mutableStateOf(
            getOfferAtTime(dailyData[todayIndex], LocalTime.of(LocalTime.now().hour, 0))
        )
    }

    // State to track updated daily data with selections
    var updatedDailyData by remember { mutableStateOf(dailyData) }

    // Function to handle slot clicks
    val handleSlotClick: (pageIndex: Int, clickedTime: LocalTime) -> Unit =
        { pageIndex, clickedTime ->
            val currentDayData = updatedDailyData[pageIndex]

            // Find if clicked time is within an offer
            val clickedOffer = getOfferAtTime(currentDayData, clickedTime)

            val updatedDay = if (clickedOffer != null) {
                // Toggle the clicked offer's selection
                val updatedOffers = currentDayData.offers.map { offer ->
                    if (offer.timeRange.startTime == clickedOffer.timeRange.startTime &&
                        offer.timeRange.endTime == clickedOffer.timeRange.endTime
                    ) {
                        offer.copy(isSelected = true)
                    } else {
                        offer.copy(isSelected = false) // Deselect all other offers
                    }
                }
                currentDayData.copy(offers = updatedOffers, isSelected = false)
            } else {
                // Regular price slot clicked - toggle day selection and deselect all offers
                val updatedOffers = currentDayData.offers.map { it.copy(isSelected = false) }
                currentDayData.copy(offers = updatedOffers, isSelected = !currentDayData.isSelected)
            }

            // Update the daily data
            updatedDailyData = updatedDailyData.toMutableList().apply {
                this[pageIndex] = updatedDay
            }

            // Update selected price
            val (newPrice, isOffer) = getPriceAtTime(updatedDay, clickedTime)
            selectedPrice = newPrice

            // Update offer selected state
            offerSelected = isOffer

            // Update selected price offer
            selectedPriceOffer = if (isOffer) {
                getOfferAtTime(updatedDay, clickedTime)
            } else {
                null
            }
        }

    val allPrices = updatedDailyData.flatMap { day ->
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CopySmall(
                    stringResource(R.string.schedule_pricing__live_pricing__label),
                    fontWeight = FontWeight.W700
                )

                TypeDropdownSelector(
                    selectedOption = selectedType.name,
                    options = listOf(ChargeType.FAST.name, ChargeType.SLOW.name),
                    onOptionSelected = {}
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TitleSmall(
                    selectedPrice.toString() + dailyData.first().currency + " " + stringResource(R.string.kwh_label),
                    color = if (offerSelected) {
                        MaterialTheme.colorScheme.brand
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(Modifier.size(10.dp))

                if (offerSelected) {
                    CopySmall(
                        dailyData[pagerState.currentPage].regularPrice.toString() + dailyData.first().currency + " " + stringResource(
                            R.string.kwh_label
                        ),
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Row(
                modifier = Modifier.padding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DayLabel()

                OfferBadge(
                    priceOffer = selectedPriceOffer,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { pageIndex ->
                DayLineChart(
                    dayData = updatedDailyData[pageIndex],
                    maxPrice = maxPrice,
                    minPrice = minPrice,
                    progress = animatedProgress,
                    minuteResolution = minuteResolution,
                    colors = colors,
                    modifier = Modifier,
                    showVerticalGridLines = showVerticalGridLines,
                    gridLineInterval = gridLineInterval,
                    gridLineDotSize = gridLineDotSize,
                    isToday = updatedDailyData[pageIndex].date == LocalDate.now(),
                    onSlotClick = { clickedTime ->
                        handleSlotClick(pageIndex, clickedTime)
                    }
                )
            }
        }
    }
}

@Composable
fun OfferBadge(priceOffer: PriceOffer?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = if (priceOffer != null) {
                    Color(0x1A279138)
                } else {
                    Color.Gray
                },
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = if (priceOffer != null) painterResource(R.drawable.ic_offer) else painterResource(
                R.drawable.ic_no_offer
            ), contentDescription = null,
            tint = if (priceOffer != null) {
                MaterialTheme.colorScheme.brand
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        CopySmall(
            text = if (priceOffer != null) {
                stringResource(R.string.offer_available)
            } else {
                stringResource(R.string.no_offer_available)
            },
            fontWeight = FontWeight.W700,
            color = if (priceOffer != null) {
                MaterialTheme.colorScheme.brand
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        if (priceOffer != null) {
            CopySmall(
                text = "%02d:%02d".format(
                    priceOffer.timeRange.startTime.hour,
                    priceOffer.timeRange.startTime.minute
                ),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            CopySmall(
                text = "%02d:%02d".format(
                    priceOffer.timeRange.endTime.hour,
                    priceOffer.timeRange.endTime.minute
                ),
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DayLabel(selectedPage: Int = 1) {
    val currentTime = LocalTime.now()
    CopySmall(
        text = when (selectedPage) {
            0 -> stringResource(R.string.schedule_pricing__yesterday)
            1 -> stringResource(R.string.schedule_pricing__today)
            2 -> stringResource(R.string.schedule_pricing__tomorrow)
            else -> "%2d:%02d"
        }.format(currentTime.hour, currentTime.minute),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun TypeDropdownSelector(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            CopySmall(selectedOption)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = "Dropdown Arrow"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
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
    gridLineDotSize: Float = 4f,
    isToday: Boolean = false,
    onSlotClick: (LocalTime) -> Unit
) {
    Column(modifier = modifier) {
        // Line chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(dayData) {
                    detectTapGestures { offset ->
                        onSlotClick(getClickedTimeByOffset(offset, minuteResolution))
                    }
                }
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
                    colors = colors,
                    isToday = isToday
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
    colors: GraphColors,
    isToday: Boolean = false
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
            currentFillPath.lineTo(x, y)

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
                currentFillPath.moveTo(x, chartBottom)
                currentFillPath.lineTo(x, lastY)
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
    listOf(
        regularSelectedFillPath,
        regularUnselectedFillPath,
        offerSelectedFillPath,
        offerUnselectedFillPath
    )
        .forEach { it.close() }

    // Draw filled areas with state-specific colors
    drawPath(regularSelectedFillPath, colors.regularSelectedArea)
    drawPath(regularUnselectedFillPath, colors.regularUnselectedArea)
    drawPath(offerSelectedFillPath, colors.offerSelectedArea)
    drawPath(offerUnselectedFillPath, colors.offerUnselectedArea)

    // Draw horizontal lines with state-specific colors
    drawPath(
        regularSelectedHorizontalPath,
        colors.regularSelectedLine,
        style = Stroke(width = 2.dp.toPx())
    )
    drawPath(
        regularUnselectedHorizontalPath,
        colors.regularUnselectedLine,
        style = Stroke(width = 2.dp.toPx())
    )
    drawPath(
        offerSelectedHorizontalPath,
        colors.offerSelectedLine,
        style = Stroke(width = 2.dp.toPx())
    )
    drawPath(
        offerUnselectedHorizontalPath,
        colors.offerUnselectedLine,
        style = Stroke(width = 2.dp.toPx())
    )

    // Draw all vertical lines with consistent color
    drawPath(verticalLinesPath, colors.verticalLine, style = Stroke(width = 2.dp.toPx()))

    // Draw current time marker if showing today
    if (isToday) {
        drawCurrentTimeMarker(
            maxPrice = maxPrice,
            minPrice = minPrice,
            minuteResolution = minuteResolution,
            chartHeight = chartHeight,
            chartBottom = chartBottom,
            colors = colors,
            dayData = dayData
        )
    }
}

private fun DrawScope.drawCurrentTimeMarker(
    maxPrice: Double,
    minPrice: Double,
    minuteResolution: Int,
    chartHeight: Float,
    chartBottom: Float,
    colors: GraphColors,
    dayData: DailyPricingData
) {
    val currentTime = LocalTime.now()
    val currentMinutes = currentTime.hour * 60 + currentTime.minute

    val minutesInDay = 24 * 60
    val dataPoints = minutesInDay / minuteResolution
    val stepWidth = size.width / dataPoints
    val priceRange = maxPrice - minPrice

    if (priceRange <= 0) return

    // Calculate x position for current time
    val currentTimeIndex = (currentMinutes / minuteResolution).toFloat()
    val x = currentTimeIndex * stepWidth

    // Get current price to determine y position
    val (currentPrice, _) = getPriceAtTime(dayData, currentTime)
    val normalizedPrice = ((currentPrice - minPrice) / priceRange).toFloat()
    val y = chartBottom - (normalizedPrice * chartHeight)

    // Draw vertical line from chart bottom to the circle marker
    drawLine(
        color = colors.currentTimeMarker,
        start = Offset(x, chartBottom),
        end = Offset(x, y),
        strokeWidth = 2.dp.toPx()
    )

    // Draw circle marker on the line
    drawCircle(
        color = colors.currentTimeMarker,
        radius = 4.dp.toPx(),
        center = Offset(x, y)
    )
    drawCircle(
        color = Color.White,
        radius = 2.dp.toPx(),
        center = Offset(x, y)
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

@PreviewLightDark
@Composable
private fun EnergyPriceLineChart_Preview() {
    ElvahChargeTheme {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnergyPriceLineChart(
                dailyData = MockData.generateThreeDayPricingData(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EnergyPriceLineChartHighResPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            minuteResolution = 5, // Every 5 minutes for smoother line
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun EnergyPriceLineChartCustomMinYAxisPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            minYAxisPrice = 0.0, // Set minimum Y-axis price to 0.0
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun EnergyPriceLineChartDottedGridPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            gridLineDotSize = 6f, // Larger dots for demonstration
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

