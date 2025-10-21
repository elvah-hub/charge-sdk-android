package de.elvah.charge.features.sites.domain.model

public data class Price(
    val energyPricePerKWh: Pricing,
    val baseFee: Pricing?,
    val blockingFee: BlockingFee?,
    val currency: String,
)
