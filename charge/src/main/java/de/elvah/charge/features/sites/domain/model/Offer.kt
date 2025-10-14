package de.elvah.charge.features.sites.domain.model

@Deprecated("Use OfferType.kt instead.")
public data class Offer(
    val price: Price,
    val type: String,
    val expiresAt: String,
    val originalPrice: Price? = null,
    val campaignEndsAt: String? = null,
    val signedOffer: String? = null,
)
