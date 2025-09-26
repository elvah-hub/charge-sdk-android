package de.elvah.charge.platform.ui.components.graph.line

import java.time.LocalTime

data class TimeRange(
    val startTime: LocalTime, // hour:minute
    val endTime: LocalTime    // hour:minute
)
