package de.elvah.charge.features.adhoc_charging.data.mapper

import de.elvah.charge.features.adhoc_charging.AdditionalCostsProto
import de.elvah.charge.features.sites.domain.model.AdditionalCosts

internal const val INVALID_TEXT = "INVALID"

private val invalidAdditionalCosts = AdditionalCosts(
    baseFee = invalidPricing,
    blockingFee = invalidBlockingFee,
    currency = INVALID_TEXT,
)

internal fun AdditionalCosts?.toProto(): AdditionalCostsProto {
    return with(this ?: invalidAdditionalCosts) {
        AdditionalCostsProto.newBuilder()
            .setBaseFee(baseFee.toProto())
            .setBlockingFee(blockingFee.toProto())
            .setCurrency(currency)
            .build()
    }
}

internal fun AdditionalCostsProto.toDomain(): AdditionalCosts? {
    return AdditionalCosts(
        baseFee = baseFee.toDomain(),
        blockingFee = blockingFee.toDomain(),
        currency = currency,
    )
        .takeIf { it.baseFee != null || it.blockingFee != null }
}
