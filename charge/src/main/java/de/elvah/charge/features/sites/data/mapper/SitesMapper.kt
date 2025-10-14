package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.model.response.site.EvseDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferPreviewCampaignDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.OfferPreviewStandardDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.SignedOfferCampaignDto
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto.SignedOfferStandardDto
import de.elvah.charge.features.sites.data.remote.model.response.site.SitesDto
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.Offer

internal fun SitesDto.toDomain(): ChargeSite {
    return ChargeSite(
        address = address.toDomain(),
        evses = evses.map { it.toDomain() },
        location = location,
        id = id,
        operatorName = operatorName,
        prevalentPowerType = prevalentPowerType
    )
}

internal fun EvseDto.toDomain(): ChargeSite.ChargePoint = ChargeSite.ChargePoint(
    evseId = evseId,
    offer = offer.toDomain(),
    powerSpecification = powerSpecification?.toDomain(),
    availability = availability.toDomain(),
    normalizedEvseId = normalizedEvseId
)

internal fun OfferTypeDto.toDomain(): Offer = when (this) {
    is OfferPreviewStandardDto -> Offer(
        price = price.toDomain(),
        originalPrice = null,
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = null,
    )

    is OfferPreviewCampaignDto -> Offer(
        price = price.toDomain(),
        originalPrice = originalPrice.toDomain(),
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = campaignEndsAt,
    )

    is SignedOfferStandardDto -> Offer(
        price = price.toDomain(),
        originalPrice = null,
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = null,
    )

    is SignedOfferCampaignDto -> Offer(
        price = price.toDomain(),
        originalPrice = originalPrice.toDomain(),
        type = type,
        expiresAt = expiresAt,
        campaignEndsAt = campaignEndsAt,
    )
}
