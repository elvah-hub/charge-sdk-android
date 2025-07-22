package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.model.response.AddressDto
import de.elvah.charge.features.sites.data.remote.model.response.BlockingFeeDto
import de.elvah.charge.features.sites.data.remote.model.response.ChargePointDto
import de.elvah.charge.features.sites.data.remote.model.response.OfferDto
import de.elvah.charge.features.sites.data.remote.model.response.PowerSpecificationDto
import de.elvah.charge.features.sites.data.remote.model.response.PriceDto
import de.elvah.charge.features.sites.data.remote.model.response.SignedOfferDto
import de.elvah.charge.features.sites.data.remote.model.response.SitesDto
import de.elvah.charge.features.sites.domain.model.ChargeSite


internal fun SitesDto<OfferDto>.toSite(): ChargeSite {
    return ChargeSite(
        address = address.toDomain(),
        evses = evses.map { it.toOffer() },
        location = location,
        id = id,
        operatorName = operatorName,
        prevalentPowerType = prevalentPowerType
    )
}

internal fun SitesDto<SignedOfferDto>.toDomain(): ChargeSite {
    return ChargeSite(
        address = address.toDomain(),
        evses = evses.map { it.toDomain() },
        location = location,
        id = id,
        operatorName = operatorName,
        prevalentPowerType = prevalentPowerType
    )
}

internal fun AddressDto.toDomain(): ChargeSite.Address = ChargeSite.Address(
    streetAddress = streetAddress,
    postalCode = postalCode,
    locality = locality
)

internal fun ChargePointDto<OfferDto>.toOffer(): ChargeSite.ChargePoint = ChargeSite.ChargePoint(
    evseId = evseId,
    offer = offer.toDomain(),
    powerSpecification = powerSpecification.toDomain(),
    normalizedEvseId = normalizedEvseId
)

internal fun ChargePointDto<SignedOfferDto>.toDomain(): ChargeSite.ChargePoint =
    ChargeSite.ChargePoint(
        evseId = evseId,
        offer = offer.toDomain(),
        powerSpecification = powerSpecification.toDomain(),
        normalizedEvseId = normalizedEvseId
    )

internal fun OfferDto.toDomain(): ChargeSite.ChargePoint.Offer = ChargeSite.ChargePoint.Offer(
    price = price.toDomain(),
    originalPrice = originalPrice?.toDomain(),
    type = type,
    expiresAt = expiresAt
)

internal fun SignedOfferDto.toDomain(): ChargeSite.ChargePoint.Offer = ChargeSite.ChargePoint.Offer(
    price = price.toDomain(),
    type = type,
    expiresAt = expiresAt,
    originalPrice = originalPrice?.toDomain(),
    campaignEndsAt = campaignEndsAt,
    signedOffer = signedOffer
)

internal fun PriceDto.toDomain(): ChargeSite.ChargePoint.Offer.Price =
    ChargeSite.ChargePoint.Offer.Price(
        energyPricePerKWh = energyPricePerKWh,
        baseFee = baseFee,
        currency = currency,
        blockingFee = blockingFee?.toDomain()
    )


internal fun BlockingFeeDto.toDomain(): ChargeSite.ChargePoint.Offer.Price.BlockingFee =
    ChargeSite.ChargePoint.Offer.Price.BlockingFee(
        pricePerMinute = pricePerMinute,
        startsAfterMinutes = startsAfterMinutes
    )

internal fun PowerSpecificationDto.toDomain(): ChargeSite.PowerSpecification =
    ChargeSite.PowerSpecification(
        maxPowerInKW = maxPowerInKW, type = type
    )
