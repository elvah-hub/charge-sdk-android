package de.elvah.charge.platform.ui.components.graph.line.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import de.elvah.charge.features.sites.ui.utils.MINUTES_IN_A_DAY
import java.time.LocalTime

internal fun PointerInputScope.getClickedTimeByOffset(
    offset: Offset,
    minuteResolution: Int
): LocalTime {
    // Convert tap position to time
    val dataPoints = MINUTES_IN_A_DAY / minuteResolution
    val stepWidth = size.width / dataPoints

    // Calculate which data point was clicked
    val clickedIndex =
        (offset.x / stepWidth).toInt().coerceIn(0, dataPoints - 1)
    val minute = clickedIndex * minuteResolution
    val hour = (minute / 60).coerceIn(0, 23)
    val minuteOfHour = (minute % 60).coerceIn(0, 59)

    val clickedTime = LocalTime.of(hour, minuteOfHour)

    return clickedTime
}
