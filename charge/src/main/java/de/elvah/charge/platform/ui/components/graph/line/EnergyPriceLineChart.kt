package de.elvah.charge.platform.ui.components.graph.line

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.utils.HOUR_MINUTE_FORMAT
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.components.CopyXLarge
import de.elvah.charge.platform.ui.components.DropdownMenuButton
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.components.buttons.DaySelector
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.DEFAULT_ANIMATION_DURATION_MS
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.DEFAULT_GRID_LINE_DOT_SIZE
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.DEFAULT_GRID_LINE_INTERVAL
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.DEFAULT_MINUTE_RESOLUTION
import de.elvah.charge.platform.ui.components.graph.line.utils.getClickedTimeByOffset
import de.elvah.charge.platform.ui.components.site.SiteDetailHeader
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copySmallBold
import de.elvah.charge.public_api.pricinggraph.GraphColorDefaults
import de.elvah.charge.public_api.pricinggraph.GraphColors
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun EnergyPriceLineChart(
    dailyData: List<DailyPricingData>,
    modifier: Modifier = Modifier,
    chargeSite: ChargeSiteUI? = null,
    colors: GraphColors = GraphColorDefaults.colors(),
    animated: Boolean = true,
    showVerticalGridLines: Boolean = true,
    gridLineInterval: Int = DEFAULT_GRID_LINE_INTERVAL,
    minuteResolution: Int = DEFAULT_MINUTE_RESOLUTION,
    minYAxisPrice: Double? = null,
    gridLineDotSize: Float = DEFAULT_GRID_LINE_DOT_SIZE
) {
    val scope = rememberCoroutineScope()

    if (dailyData.isEmpty()) return

    // Default value is the today page and the slot based on the current hour
    val todayIndex = dailyData.indexOfFirst { it.date == LocalDate.now() }.takeIf { it != -1 } ?: 1

    val pagerState = rememberPagerState(
        initialPage = todayIndex,
        pageCount = { dailyData.size }
    )
    var selectedType by remember { mutableStateOf(ChargeType.VERY_FAST) }
    var selectedPrice by remember {
        mutableDoubleStateOf(
            getPriceAtTimeFromSlots(
                dailyData[todayIndex],
                LocalTime.of(LocalTime.now().hour, 0)
            )
        )
    }
    var offerSelected by remember { mutableStateOf(false) }
    var selectedPriceOffer by remember {
        mutableStateOf(
            getOfferAtTimeFromSlots(dailyData[todayIndex], LocalTime.of(LocalTime.now().hour, 0))
        )
    }

    // State to track updated daily data with selections
    var updatedDailyData by remember { mutableStateOf(dailyData) }

    // Function to handle slot clicks
    val handleSlotClick: (pageIndex: Int, clickedTime: LocalTime) -> Unit =
        { pageIndex, clickedTime ->
            val currentDayData = updatedDailyData[pageIndex]

            // Find the clicked slot
            val clickedSlot = getSlotAtTime(currentDayData, clickedTime)

            val updatedSlots = if (clickedSlot != null) {
                currentDayData.slots.map { slot ->
                    when {
                        // Clicked on this slot - toggle its selection
                        slot.startTime == clickedSlot.startTime && slot.endTime == clickedSlot.endTime -> {
                            when (slot) {
                                is PriceSlot.RegularPriceSlot -> slot.copy(isSelected = !slot.isSelected)
                                is PriceSlot.OfferPriceSlot -> slot.copy(isSelected = !slot.isSelected)
                            }
                        }
                        // Deselect all other slots
                        else -> {
                            when (slot) {
                                is PriceSlot.RegularPriceSlot -> slot.copy(isSelected = false)
                                is PriceSlot.OfferPriceSlot -> slot.copy(isSelected = false)
                            }
                        }
                    }
                }
            } else {
                currentDayData.slots
            }

            val updatedDay = currentDayData.withUpdatedSlots(updatedSlots).copy(isSelected = false)

            // Update the daily data
            updatedDailyData = updatedDailyData.toMutableList().apply {
                this[pageIndex] = updatedDay
            }

            // Update selected price based on current selection state
            val selectedSlot = updatedSlots.find { it.isSelected }
            if (selectedSlot != null) {
                selectedPrice = selectedSlot.price
                offerSelected = selectedSlot is PriceSlot.OfferPriceSlot
                selectedPriceOffer = if (selectedSlot is PriceSlot.OfferPriceSlot) {
                    // Convert slot back to PriceOffer for display compatibility
                    PriceOffer(
                        timeRange = TimeRange(selectedSlot.startTime, selectedSlot.endTime),
                        discountedPrice = selectedSlot.price,
                        isSelected = true
                    )
                } else null
            } else {
                // No selection - show current price
                val currentTime = LocalTime.now()
                val isToday = updatedDay.date == LocalDate.now()
                if (isToday) {
                    val currentSlot = getSlotAtTime(updatedDay, currentTime)
                    selectedPrice = currentSlot?.price ?: updatedDay.regularPrice
                    offerSelected = currentSlot is PriceSlot.OfferPriceSlot
                    selectedPriceOffer = if (currentSlot is PriceSlot.OfferPriceSlot) {
                        PriceOffer(
                            timeRange = TimeRange(currentSlot.startTime, currentSlot.endTime),
                            discountedPrice = currentSlot.price,
                            isSelected = false
                        )
                    } else null
                } else {
                    selectedPrice = updatedDay.regularPrice
                    offerSelected = false
                    selectedPriceOffer = null
                }
            }
        }

    val allPrices = updatedDailyData.flatMap { day ->
        day.slots.map { it.price }
    }
    val maxPrice = allPrices.maxOf { it }
    val calculatedMinPrice = allPrices.minOf { it }
    val minPrice = minYAxisPrice?.let { userMin ->
        minOf(userMin, calculatedMinPrice)
    } ?: calculatedMinPrice

    val animatedProgress by animateFloatAsState(
        targetValue = if (animated) 1f else 1f,
        animationSpec = tween(durationMillis = DEFAULT_ANIMATION_DURATION_MS),
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
            chargeSite?.let {
                SiteDetailHeader(
                    operatorName = chargeSite.cpoName,
                    address = chargeSite.address.streetAddress.joinToString(separator = " "),
                    coordinates = Pair(chargeSite.lat, chargeSite.lng)
                )
            }

            LivePricingHeader(selectedType)

            LivePricingPrice(
                selectedPrice = selectedPrice,
                offerSelected = offerSelected,
                currency = updatedDailyData.first().currency,
                regularPrice = updatedDailyData[pagerState.currentPage].regularPrice,
                textColor = colors.offerSelectedLine
            )

            LivePriceTimeSlot(selectedPriceOffer, pagerState.currentPage)

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            DaySelector(pagerState.currentPage, modifier = Modifier.fillMaxWidth()) {
                scope.launch {
                    pagerState.animateScrollToPage(it)
                }
            }
        }
    }
}

@Composable
private fun LivePriceTimeSlot(selectedPriceOffer: PriceOffer?, selectedPage: Int) {
    Row(
        modifier = Modifier.padding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DayLabel(selectedPage = selectedPage)

        OfferBadge(
            priceOffer = selectedPriceOffer,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun LivePricingPrice(
    selectedPrice: Double,
    offerSelected: Boolean,
    currency: String,
    regularPrice: Double,
    textColor: Color = MaterialTheme.colorSchemeExtended.brand
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TitleSmall(
            text = "$selectedPrice$currency " + stringResource(
                R.string.kwh_label
            ),
            color = if (offerSelected) {
                textColor
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        if (offerSelected) {
            CopySmall(
                text = "$regularPrice$currency " + stringResource(
                    R.string.kwh_label
                ),
                textDecoration = TextDecoration.LineThrough
            )
        }
    }
}

@Composable
private fun LivePricingHeader(selectedType: ChargeType) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopySmall(
            text = stringResource(R.string.live_pricing_label),
            fontWeight = FontWeight.W700
        )

        TypeDropdownSelector(
            selectedOption = selectedType.text,
            options = listOf(
                PlugType(
                    title = "CSS",
                    subtitle = "Very fast (350kW)"
                ),
                PlugType(
                    title = "Type 2",
                    subtitle = "fast (22kW)"
                ),
            ),
        )
    }
}

@Composable
private fun OfferBadge(
    priceOffer: PriceOffer?,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorSchemeExtended.brand
) {
    FlowRow(
        modifier = modifier
            .background(
                color = if (priceOffer != null) {
                    color
                } else {
                    MaterialTheme.colorScheme.primary
                }.copy(alpha = 0.1f),
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 8.dp, vertical = 10.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = if (priceOffer != null) painterResource(R.drawable.ic_offer) else painterResource(
                R.drawable.ic_no_offer
            ), contentDescription = null,
            tint = if (priceOffer != null) {
                color
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
        CopySmall(
            text = if (priceOffer != null) {
                stringResource(R.string.generic_offer_available)
            } else {
                stringResource(R.string.no_offer_available_label)
            } + ":",
            fontWeight = FontWeight.W700,
            color = if (priceOffer != null) {
                color
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
        if (priceOffer != null) {
            HourSlot(priceOffer.timeRange)
        }
    }
}

@Composable
private fun HourSlot(timeRange: TimeRange, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CopySmall(
            text = HOUR_MINUTE_FORMAT.format(
                timeRange.startTime.hour,
                timeRange.startTime.minute
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
            text = HOUR_MINUTE_FORMAT.format(
                timeRange.endTime.hour,
                timeRange.endTime.minute
            ),
            fontWeight = FontWeight.W700,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
internal fun DayLabel(selectedPage: Int) {
    val currentTime = LocalTime.now()
    val formattedTime = "%2d:%02d".format(currentTime.hour, currentTime.minute)

    Text(
        text = when (selectedPage) {
            0 -> stringResource(R.string.generic_yesterday)
            2 -> stringResource(R.string.generic_tomorrow)
            else -> stringResource(R.string.generic_today)
        } + " $formattedTime",
        style = copySmallBold,
        color = MaterialTheme.colorScheme.primary
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeDropdownSelector(
    selectedOption: String,
    options: List<PlugType>,
) {
    val lockedSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    DropdownMenuButton(
        selectedOption = selectedOption,
        onClick = {
            scope.launch {
                lockedSheetState.show()
            }
        }
    )

    if (lockedSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    lockedSheetState.hide()
                }
            }, sheetState = lockedSheetState
        ) {
            TypeModalContent(options, onCloseClick = {
                scope.launch {
                    lockedSheetState.hide()
                }
            }
            )
        }
    }
}

@Composable
private fun TypeModalContent(
    options: List<PlugType>,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit
) {
    Column(modifier = modifier) {
        TypeModalContentHeader(onClick = onCloseClick)
        TypeModalContentList(options)
    }
}

@Composable
private fun TypeModalContentHeader(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopyXLarge(
            text = stringResource(R.string.live_pricing_label),
            fontWeight = FontWeight.W700
        )

        CloseIcon(onClick = onClick)
    }
}

@Composable
private fun TypeModalContentList(options: List<PlugType>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        options.forEach {
            TypeModalContentListItem(it)
        }
    }
}

@Composable
private fun TypeModalContentListItem(option: PlugType, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopyMedium(
            text = option.title,
            modifier = Modifier,
            fontWeight = FontWeight.W700
        )
        Spacer(modifier = Modifier.size(4.dp))
        CopyMedium(
            text = option.subtitle,
            modifier = Modifier
        )
    }
}

internal data class PlugType(
    val title: String,
    val subtitle: String
)

@Composable
private fun CloseIcon(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
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
        // Line chart with extra space for hour labels
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

    }
}

// Helper function to get slot at specific time
private fun getSlotAtTime(dayData: DailyPricingData, time: LocalTime): PriceSlot? {
    return dayData.slots.find { slot ->
        time >= slot.startTime && time < slot.endTime
    }
}

// Helper function to get price at specific time from slots
private fun getPriceAtTimeFromSlots(dayData: DailyPricingData, time: LocalTime): Double {
    val slot = getSlotAtTime(dayData, time)
    return slot?.price ?: dayData.regularPrice
}

// Helper function to get offer at specific time from slots (for backward compatibility)
private fun getOfferAtTimeFromSlots(dayData: DailyPricingData, time: LocalTime): PriceOffer? {
    val slot = getSlotAtTime(dayData, time)
    return if (slot is PriceSlot.OfferPriceSlot) {
        PriceOffer(
            timeRange = TimeRange(slot.startTime, slot.endTime),
            discountedPrice = slot.price,
            isSelected = slot.isSelected
        )
    } else null
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

@PreviewLightDark
@Composable
private fun EnergyPriceLineChartCustomColorsPreview() {
    ElvahChargeTheme {
        EnergyPriceLineChart(
            dailyData = MockData.generateThreeDayPricingData(),
            colors = GraphColorDefaults.colors(
                offerSelectedLine = Color(0xFF2196F3), // Blue
                offerSelectedArea = Color(0xFF2196F3).copy(alpha = 0.3f),
                offerUnselectedLine = Color(0xFF2196F3).copy(alpha = 0.6f),
                offerUnselectedArea = Color(0xFF2196F3).copy(alpha = 0.2f),
                regularSelectedLine = Color(0xFF9C27B0), // Purple
                regularSelectedArea = Color(0xFF9C27B0).copy(alpha = 0.3f),
                regularUnselectedLine = Color(0xFF9C27B0).copy(alpha = 0.4f),
                regularUnselectedArea = Color(0xFF9C27B0).copy(alpha = 0.2f),
                verticalLine = Color(0xFF757575).copy(alpha = 0.8f),
                currentTimeMarker = Color(0xFFFF5722) // Orange
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

