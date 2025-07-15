package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.AddressDto
import de.elvah.charge.features.sites.data.remote.BlockingFeeDto
import de.elvah.charge.features.sites.data.remote.DataItemDto
import de.elvah.charge.features.sites.data.remote.EvsesItemDto
import de.elvah.charge.features.sites.data.remote.OfferDto
import de.elvah.charge.features.sites.data.remote.PowerSpecificationDto
import de.elvah.charge.features.sites.data.remote.PriceDto
import de.elvah.charge.features.sites.data.remote.SitesDto
import de.elvah.charge.features.sites.domain.model.Address
import de.elvah.charge.features.sites.domain.model.BlockingFee
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.EvsesItem
import de.elvah.charge.features.sites.domain.model.Offer
import de.elvah.charge.features.sites.domain.model.PowerSpecification
import de.elvah.charge.features.sites.domain.model.Price

internal fun SitesDto.toDomain(): List<ChargeSite> {
    return this.data.map { it.toDomain() }
}

internal fun DataItemDto.toDomain(): ChargeSite {
    return ChargeSite(
        address = address.toDomain(),
        evses = evses.map { it.toDomain() },
        location = location,
        id = id,
        operatorName = operatorName,
        prevalentPowerType = prevalentPowerType
    )
}

internal fun AddressDto.toDomain(): Address = Address(
    streetAddress = streetAddress,
    postalCode = postalCode,
    locality = locality
)

internal fun EvsesItemDto.toDomain(): EvsesItem = EvsesItem(
    evseId = evseId,
    offer = offer.toDomain(),
    powerSpecification = powerSpecification.toDomain(),
    normalizedEvseId = normalizedEvseId
)

internal fun OfferDto.toDomain(): Offer = Offer(
    price = price.toDomain(),
    type = type,
    expiresAt = expiresAt
)

internal fun PriceDto.toDomain(): Price = Price(
    energyPricePerKWh = energyPricePerKWh,
    baseFee = baseFee,
    currency = currency,
    blockingFee = blockingFee.toDomain()
)

internal fun BlockingFeeDto.toDomain(): BlockingFee = BlockingFee(
    pricePerMinute = pricePerMinute,
    startsAfterMinutes = startsAfterMinutes
)

internal fun PowerSpecificationDto.toDomain(): PowerSpecification = PowerSpecification(
    maxPowerInKW = maxPowerInKW, type = type
)
