package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferPreviewCampaignDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferPreviewStandardDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.SignedOfferCampaignDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.SignedOfferStandardDto
import de.elvah.charge.features.sites.domain.model.Offer

internal fun OfferTypeDto.toDomain(): Offer = when (this) {
    is OfferPreviewStandardDto -> Offer(
        price = price.toDomain(),
        originalPrice = null,
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = null,
        signedOffer = null,
    )

    is OfferPreviewCampaignDto -> Offer(
        price = price.toDomain(),
        originalPrice = originalPrice.toDomain(),
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = campaignEndsAt,
        signedOffer = null,
    )

    is SignedOfferStandardDto -> Offer(
        price = price.toDomain(),
        originalPrice = null,
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = null,
        signedOffer = signedOffer,
    )

    is SignedOfferCampaignDto -> Offer(
        price = price.toDomain(),
        originalPrice = originalPrice.toDomain(),
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = campaignEndsAt,
        signedOffer = signedOffer,
    )
}
