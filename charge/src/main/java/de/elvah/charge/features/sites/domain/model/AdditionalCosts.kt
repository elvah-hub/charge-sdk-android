package de.elvah.charge.features.sites.domain.model

internal data class AdditionalCosts(
    val baseFee: Pricing?,
    val blockingFee: BlockingFee?,
    val currency: String,
)
