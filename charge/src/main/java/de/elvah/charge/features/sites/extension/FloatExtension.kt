package de.elvah.charge.features.sites.extension

import java.math.RoundingMode
import java.text.DecimalFormat

fun Float.formatKW(
    includeMinusEqualChar: Boolean = false,
): String {
    val pattern = StringBuilder()
        .apply {
            if (includeMinusEqualChar) {
                append("≤")
                append(" ")
            }

            append("0.#kW")
        }
        .toString()

    return DecimalFormat(pattern)
        .apply { roundingMode = RoundingMode.HALF_UP }
        .format(this)
}
