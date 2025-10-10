package de.elvah.charge.platform.ui.components.graph.line

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.CHART_BOTTOM_MULTIPLIER
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.CHART_HEIGHT_MULTIPLIER
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.CIRCLE_MARKER_INNER_RADIUS_DP
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.CIRCLE_MARKER_RADIUS_DP
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.GRID_LINE_ALPHA
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.GRID_LINE_STROKE_WIDTH_DP
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.HOURS_IN_DAY
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.LINE_STROKE_WIDTH_DP
import de.elvah.charge.platform.ui.components.graph.line.GraphConstants.MINUTES_IN_DAY
import java.time.LocalTime

/**
 * Contains all drawing functions for the graph components.
 * This file centralizes the Canvas drawing logic to reduce complexity in the main components.
 */

/**
 * Draws vertical grid lines on the chart canvas
 */
internal fun DrawScope.drawGridLines(
    showVerticalGridLines: Boolean,
    gridLineInterval: Int,
    gridLineDotSize: Float,
) {
    if (!showVerticalGridLines) return

    val gridColor = Color.Gray.copy(alpha = GRID_LINE_ALPHA)
    val stepWidth = size.width / HOURS_IN_DAY
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(gridLineDotSize, gridLineDotSize), 0f)
    
    // Calculate chart area height using the same multiplier as the step chart
    val chartHeight = size.height * CHART_HEIGHT_MULTIPLIER
    val chartBottom = size.height * CHART_BOTTOM_MULTIPLIER
    
    // Text properties
    val textColor = Color.Gray.copy(alpha = 0.8f)
    val textSize = 12.sp.toPx()
    val textPaint = android.graphics.Paint().apply {
        color = textColor.toArgb()
        this.textSize = textSize
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = false
    }

    for (hour in 0..HOURS_IN_DAY step gridLineInterval) {
        val x = hour * stepWidth
        
        // Draw grid line only up to chart bottom
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, chartBottom),
            strokeWidth = GRID_LINE_STROKE_WIDTH_DP.dp.toPx(),
            pathEffect = pathEffect
        )
        
        // Draw hour text in the space below chart bottom
        if (hour < HOURS_IN_DAY) { // Don't draw text for the last line at hour 24
            val textY = chartBottom + textSize + 12.dp.toPx()
            // Make sure text fits within canvas bounds
            if (textY < size.height) {
                drawContext.canvas.nativeCanvas.drawText(
                    "$hour",
                    x,
                    textY,
                    textPaint
                )
            }
        }
    }
}

/**
 * Draws the step line chart with filled areas for different pricing states
 */
internal fun DrawScope.drawStepLineChart(
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

    val dataPoints = MINUTES_IN_DAY / minuteResolution
    val stepWidth = size.width / dataPoints
    val chartHeight = size.height * CHART_HEIGHT_MULTIPLIER
    val chartBottom = size.height * CHART_BOTTOM_MULTIPLIER

    val drawingPaths = createDrawingPaths()
    val drawingContext = StepLineDrawingContext(
        dayData = dayData,
        maxPrice = maxPrice,
        minPrice = minPrice,
        priceRange = priceRange,
        progress = progress,
        minuteResolution = minuteResolution,
        stepWidth = stepWidth,
        chartHeight = chartHeight,
        chartBottom = chartBottom,
        colors = colors,
        paths = drawingPaths
    )

    drawStepLineChartPaths(drawingContext)
    drawStepLineChartLines(drawingContext)

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

/**
 * Draws the current time marker on today's chart
 */
internal fun DrawScope.drawCurrentTimeMarker(
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
    val dataPoints = MINUTES_IN_DAY / minuteResolution
    val stepWidth = size.width / dataPoints
    val priceRange = maxPrice - minPrice

    if (priceRange <= 0) return

    val currentTimeIndex = (currentMinutes / minuteResolution).toFloat()
    val x = currentTimeIndex * stepWidth

    val slot = getSlotAtTime(dayData, currentTime)
    val currentPrice = slot?.price ?: dayData.regularPrice
    val normalizedPrice = ((currentPrice - minPrice) / priceRange).toFloat()
    val y = chartBottom - (normalizedPrice * chartHeight)

    drawLine(
        color = colors.currentTimeMarker,
        start = Offset(x, chartBottom),
        end = Offset(x, y),
        strokeWidth = LINE_STROKE_WIDTH_DP.dp.toPx()
    )

    drawCircle(
        color = colors.currentTimeMarker,
        radius = CIRCLE_MARKER_RADIUS_DP.dp.toPx(),
        center = Offset(x, y)
    )
    drawCircle(
        color = Color.White,
        radius = CIRCLE_MARKER_INNER_RADIUS_DP.dp.toPx(),
        center = Offset(x, y)
    )
}

/**
 * Data class to hold all drawing paths for step line chart
 */
private data class DrawingPaths(
    val regularSelectedHorizontal: Path = Path(),
    val regularUnselectedHorizontal: Path = Path(),
    val offerSelectedHorizontal: Path = Path(),
    val offerUnselectedHorizontal: Path = Path(),
    val verticalLines: Path = Path(),
    val regularSelectedFill: Path = Path(),
    val regularUnselectedFill: Path = Path(),
    val offerSelectedFill: Path = Path(),
    val offerUnselectedFill: Path = Path()
)

/**
 * Context data for step line drawing operations
 */
private data class StepLineDrawingContext(
    val dayData: DailyPricingData,
    val maxPrice: Double,
    val minPrice: Double,
    val priceRange: Double,
    val progress: Float,
    val minuteResolution: Int,
    val stepWidth: Float,
    val chartHeight: Float,
    val chartBottom: Float,
    val colors: GraphColors,
    val paths: DrawingPaths
)

/**
 * Creates and initializes all drawing paths
 */
private fun createDrawingPaths(): DrawingPaths = DrawingPaths()

/**
 * Draws the step line chart paths (filled areas and lines)
 */
private fun drawStepLineChartPaths(context: StepLineDrawingContext) {
    val dataPoints = MINUTES_IN_DAY / context.minuteResolution
    var isFirstPoint = true
    var currentFillPath: Path? = null
    var lastWasOffer = false
    var lastWasSelected = false
    var lastY = 0f

    // Initialize fill paths
    initializeFillPaths(context.paths, context.chartBottom)

    for (i in 0 until (dataPoints * context.progress).toInt()) {
        val minute = i * context.minuteResolution
        val hour = minute / 60
        val minuteOfHour = minute % 60
        val currentTime = LocalTime.of(hour, minuteOfHour)

        val slot = getSlotAtTime(context.dayData, currentTime)
        val currentPrice = slot?.price ?: context.dayData.regularPrice
        val isOffer = slot is PriceSlot.OfferPriceSlot
        val isSelected = slot?.isSelected ?: false

        val x = i * context.stepWidth
        val normalizedPrice = ((currentPrice - context.minPrice) / context.priceRange).toFloat()
        val y = context.chartBottom - (normalizedPrice * context.chartHeight)

        if (isFirstPoint) {
            currentFillPath = selectInitialFillPath(context.paths, isOffer, isSelected)
            currentFillPath.lineTo(x, y)
            isFirstPoint = false
            lastWasOffer = isOffer
            lastWasSelected = isSelected
            lastY = y
        } else {
            addHorizontalLine(context.paths, lastWasOffer, lastWasSelected, i, context.stepWidth, lastY)
            addVerticalLineIfNeeded(context.paths.verticalLines, x, lastY, y)
            
            currentFillPath?.lineTo(x, lastY)
            
            val stateChanged = (isOffer != lastWasOffer) || (isSelected != lastWasSelected)
            if (stateChanged) {
                currentFillPath?.lineTo(x, context.chartBottom)
                currentFillPath = selectFillPath(context.paths, isOffer, isSelected)
                currentFillPath.moveTo(x, context.chartBottom)
                currentFillPath.lineTo(x, lastY)
            }

            currentFillPath?.lineTo(x, y)
            lastWasOffer = isOffer
            lastWasSelected = isSelected
            lastY = y
        }
    }

    // Add final horizontal line to complete the day (extend to the end of the chart)
    if (!isFirstPoint) {
        val finalIndex = (dataPoints * context.progress).toInt()
        addHorizontalLine(context.paths, lastWasOffer, lastWasSelected, finalIndex, context.stepWidth, lastY)
        currentFillPath?.lineTo(context.stepWidth * dataPoints, lastY)
    }

    closeFillPaths(context.paths, dataPoints, context.stepWidth, context.chartBottom)
}

/**
 * Draws all the step line chart lines with appropriate colors
 */
private fun DrawScope.drawStepLineChartLines(context: StepLineDrawingContext) {
    val strokeStyle = Stroke(width = LINE_STROKE_WIDTH_DP.dp.toPx())

    // Draw filled areas
    drawPath(context.paths.regularSelectedFill, context.colors.regularSelectedArea)
    drawPath(context.paths.regularUnselectedFill, context.colors.regularUnselectedArea)
    drawPath(context.paths.offerSelectedFill, context.colors.offerSelectedArea)
    drawPath(context.paths.offerUnselectedFill, context.colors.offerUnselectedArea)

    // Draw horizontal lines
    drawPath(context.paths.regularSelectedHorizontal, context.colors.regularSelectedLine, style = strokeStyle)
    drawPath(context.paths.regularUnselectedHorizontal, context.colors.regularUnselectedLine, style = strokeStyle)
    drawPath(context.paths.offerSelectedHorizontal, context.colors.offerSelectedLine, style = strokeStyle)
    drawPath(context.paths.offerUnselectedHorizontal, context.colors.offerUnselectedLine, style = strokeStyle)

    // Draw vertical lines
    drawPath(context.paths.verticalLines, context.colors.verticalLine, style = strokeStyle)
}

/**
 * Helper functions for path management
 */
private fun initializeFillPaths(paths: DrawingPaths, chartBottom: Float) {
    paths.regularSelectedFill.moveTo(0f, chartBottom)
    paths.regularUnselectedFill.moveTo(0f, chartBottom)
    paths.offerSelectedFill.moveTo(0f, chartBottom)
    paths.offerUnselectedFill.moveTo(0f, chartBottom)
}

private fun selectInitialFillPath(paths: DrawingPaths, isOffer: Boolean, isSelected: Boolean): Path {
    return when {
        isOffer && isSelected -> paths.offerSelectedFill
        isOffer && !isSelected -> paths.offerUnselectedFill
        !isOffer && isSelected -> paths.regularSelectedFill
        else -> paths.regularUnselectedFill
    }
}

private fun selectFillPath(paths: DrawingPaths, isOffer: Boolean, isSelected: Boolean): Path {
    return when {
        isOffer && isSelected -> paths.offerSelectedFill
        isOffer && !isSelected -> paths.offerUnselectedFill
        !isOffer && isSelected -> paths.regularSelectedFill
        else -> paths.regularUnselectedFill
    }
}

private fun addHorizontalLine(paths: DrawingPaths, lastWasOffer: Boolean, lastWasSelected: Boolean, i: Int, stepWidth: Float, lastY: Float) {
    val horizontalPath = when {
        lastWasOffer && lastWasSelected -> paths.offerSelectedHorizontal
        lastWasOffer && !lastWasSelected -> paths.offerUnselectedHorizontal
        !lastWasOffer && lastWasSelected -> paths.regularSelectedHorizontal
        else -> paths.regularUnselectedHorizontal
    }
    horizontalPath.moveTo((i - 1) * stepWidth, lastY)
    horizontalPath.lineTo(i * stepWidth, lastY)
}

private fun addVerticalLineIfNeeded(verticalLinesPath: Path, x: Float, lastY: Float, y: Float) {
    if (lastY != y) {
        verticalLinesPath.moveTo(x, lastY)
        verticalLinesPath.lineTo(x, y)
    }
}

private fun closeFillPaths(paths: DrawingPaths, dataPoints: Int, stepWidth: Float, chartBottom: Float) {
    val lastX = stepWidth * dataPoints
    paths.regularSelectedFill.lineTo(lastX, chartBottom)
    paths.regularUnselectedFill.lineTo(lastX, chartBottom)
    paths.offerSelectedFill.lineTo(lastX, chartBottom)
    paths.offerUnselectedFill.lineTo(lastX, chartBottom)

    listOf(
        paths.regularSelectedFill,
        paths.regularUnselectedFill,
        paths.offerSelectedFill,
        paths.offerUnselectedFill
    ).forEach { it.close() }
}

private fun getSlotAtTime(dayData: DailyPricingData, time: LocalTime): PriceSlot? {
    return dayData.slots.find { slot ->
        time >= slot.startTime && time < slot.endTime
    }
}
