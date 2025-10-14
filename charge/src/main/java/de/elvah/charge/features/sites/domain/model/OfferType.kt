package de.elvah.charge.features.sites.domain.model

internal sealed interface OfferType {

    data class OfferPreviewStandard(
        val expiresAt: String,
        val price: Price,
        val type: String,
    ) : OfferType

    data class OfferPreviewCampaign(
        val campaignEndsAt: String,
        val expiresAt: String,
        val originalPrice: Price,
        val price: Price,
        val type: String,
    ) : OfferType

    data class SignedOfferStandard(
        val expiresAt: String,
        val price: Price,
        val signedOffer: String,
        val type: String,
    ) : OfferType

    data class SignedOfferCampaign(
        val campaignEndsAt: String,
        val expiresAt: String,
        val originalPrice: Price,
        val price: Price,
        val signedOffer: String,
        val type: String,
    ) : OfferType
}
