package de.elvah.charge.features.sites.ui.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal fun String.toLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDateTime? {
    return runCatching {
        Instant.parse(this)
            .toLocalDateTime(timeZone)
    }.getOrNull()
}

internal const val MINUTES_IN_A_DAY = 24 * 60
