package de.elvah.charge.features.sites.extension

import de.elvah.charge.features.sites.domain.model.Price
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

internal fun Price.formatted(
    includeCurrency: Boolean = true,
): String =
    value.formattedPrice(
        currency = currency,
        includeCurrency = includeCurrency,
    )

private fun Double.formattedPrice(
    locale: Locale = Locale.getDefault(),
    currency: String,
    includeCurrency: Boolean = true,
): String {
    val format = if (includeCurrency) {
        NumberFormat.getCurrencyInstance(locale)

    } else {
        NumberFormat.getInstance(locale)
    }

    return format
        .apply {
            // only show fractions when we need them
            minimumFractionDigits = if (mod(1.0) == 0.0) {
                0
            } else {
                2
            }

            roundingMode = RoundingMode.HALF_UP

            // only set currency when we could parse it
            currency.asCurrency?.let {
                this.currency = it
            }
        }
        .format(this)
}

private val String.asCurrency: Currency?
    get() {
        return try {
            Currency.getInstance(this)

        } catch (_: NullPointerException) {
            null

        } catch (_: IllegalArgumentException) {
            null
        }
    }
