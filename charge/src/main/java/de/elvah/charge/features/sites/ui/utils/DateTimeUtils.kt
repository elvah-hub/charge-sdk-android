package de.elvah.charge.features.sites.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import de.elvah.charge.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun parseDate(date: String): String {
    val dateTimeFormat = remember { DateTimeFormatter.ofPattern(DATE_FORMAT) }

    val currentDateTime = LocalDateTime.now()
    val dealDateTime = remember(date) { LocalDateTime.parse(date, dateTimeFormat) }

    val timeDifference = ChronoUnit.SECONDS.between(currentDateTime, dealDateTime)

    return when {
        timeDifference <= 0 -> stringResource(R.string.deal_expired_label)
        timeDifference < SECONDS_IN_MINUTE -> stringResource(
            R.string.ends_in_seconds_placeholder,
            timeDifference
        )

        timeDifference < SECONDS_IN_HOUR -> stringResource(
            R.string.ends_in_minutes_placeholder,
            timeDifference / SECONDS_IN_MINUTE,
            timeDifference % SECONDS_IN_MINUTE
        )

        timeDifference < SECONDS_IN_DAY -> stringResource(
            R.string.ends_in_hours_placeholder,
            timeDifference / SECONDS_IN_HOUR,
            (timeDifference % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
        )

        else -> stringResource(R.string.ends_in_days_placeholder, timeDifference / SECONDS_IN_DAY)
    }
}

fun getSecondsTillDealEnd(date: String): Long {
    val dateTimeFormat = DateTimeFormatter.ofPattern(DATE_FORMAT)

    val currentDateTime = LocalDateTime.now()
    val dealDateTime = LocalDateTime.parse(date, dateTimeFormat)

    val timeDifference = ChronoUnit.SECONDS.between(currentDateTime, dealDateTime)

    return timeDifference
}

private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'"

private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60
private const val SECONDS_IN_DAY = 86400
