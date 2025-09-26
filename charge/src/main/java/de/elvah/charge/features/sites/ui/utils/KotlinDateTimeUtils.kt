package de.elvah.charge.features.sites.ui.utils

import kotlinx.datetime.LocalDateTime

internal fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}

internal const val MINUTES_IN_A_DAY = 24 * 60
