package de.elvah.charge.features.sites.ui.utils

import kotlinx.datetime.LocalDateTime

internal fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}
