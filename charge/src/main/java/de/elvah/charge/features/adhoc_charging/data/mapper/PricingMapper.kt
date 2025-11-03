package de.elvah.charge.features.adhoc_charging.data.mapper

import de.elvah.charge.features.adhoc_charging.PricingProto
import de.elvah.charge.features.sites.domain.model.Pricing

internal val invalidPricing = Pricing(0.0, INVALID_TEXT)

internal fun Pricing?.toProto(): PricingProto {
    return with(this ?: invalidPricing) {
        PricingProto.newBuilder()
            .setValue(value)
            .setCurrency(currency)
            .build()
    }
}

internal fun PricingProto.toDomain(): Pricing? {
    return Pricing(
        value = value,
        currency = currency,
    )
        .takeIf { it != invalidPricing }
}
