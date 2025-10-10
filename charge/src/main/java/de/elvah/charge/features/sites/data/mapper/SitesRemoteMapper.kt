package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.model.response.AddressDto
import de.elvah.charge.features.sites.data.remote.model.response.BlockingFeeDto
import de.elvah.charge.features.sites.data.remote.model.response.ChargePointAvailabilityDto
import de.elvah.charge.features.sites.data.remote.model.response.ChargePointDto
import de.elvah.charge.features.sites.data.remote.model.response.DailyPricingDto
import de.elvah.charge.features.sites.data.remote.model.response.DayDto
import de.elvah.charge.features.sites.data.remote.model.response.OfferDto
import de.elvah.charge.features.sites.data.remote.model.response.PowerSpecificationDto
import de.elvah.charge.features.sites.data.remote.model.response.PriceDto
import de.elvah.charge.features.sites.data.remote.model.response.ScheduledPricingDto
import de.elvah.charge.features.sites.data.remote.model.response.SignedOfferDto
import de.elvah.charge.features.sites.data.remote.model.response.SitesDto
import de.elvah.charge.features.sites.data.remote.model.response.TimeSlotsItemDto
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing


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
    postalCode = postalCode.orEmpty(),
    locality = locality.orEmpty()
)

internal fun ChargePointDto<OfferDto>.toOffer(): ChargeSite.ChargePoint = ChargeSite.ChargePoint(
    evseId = evseId,
    offer = offer.toDomain(),
    powerSpecification = powerSpecification?.toDomain(),
    availability = availability.toDomain(),
    normalizedEvseId = normalizedEvseId
)

internal fun ChargePointDto<SignedOfferDto>.toDomain(): ChargeSite.ChargePoint =
    ChargeSite.ChargePoint(
        evseId = evseId,
        offer = offer.toDomain(),
        powerSpecification = powerSpecification?.toDomain(),
        availability = availability.toDomain(),
        normalizedEvseId = normalizedEvseId
    )

internal fun ChargePointAvailabilityDto.toDomain(): ChargePointAvailability =
    when (this) {
        ChargePointAvailabilityDto.UNAVAILABLE -> ChargePointAvailability.UNAVAILABLE
        ChargePointAvailabilityDto.AVAILABLE -> ChargePointAvailability.AVAILABLE
        ChargePointAvailabilityDto.OUT_OF_SERVICE -> ChargePointAvailability.OUT_OF_SERVICE
        ChargePointAvailabilityDto.UNKNOWN -> ChargePointAvailability.UNKNOWN
    }

internal fun OfferDto.toDomain(): ChargeSite.ChargePoint.Offer = ChargeSite.ChargePoint.Offer(
    price = price.toDomain(),
    originalPrice = originalPrice?.toDomain(),
    type = type,
    expiresAt = expiresAt,
    campaignEndsAt = campaignEndsAt,
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

internal fun ScheduledPricingDto.toDomain(): ScheduledPricing = ScheduledPricing(
    dailyPricing = dailyPricing.toDomain(),
    standardPrice = standardPrice.toScheduledPricingPrice()
)

internal fun DailyPricingDto.toDomain(): ScheduledPricing.DailyPricing =
    ScheduledPricing.DailyPricing(
        yesterday = yesterday.toDomain(),
        today = today.toDomain(),
        tomorrow = tomorrow.toDomain()
    )

internal fun DayDto.toDomain(): ScheduledPricing.Day = ScheduledPricing.Day(
    lowestPrice = lowestPrice.toScheduledPricingPrice(),
    trend = trend,
    timeSlots = timeSlots.map { it.toDomain() }
)

internal fun TimeSlotsItemDto.toDomain(): ScheduledPricing.TimeSlot = ScheduledPricing.TimeSlot(
    isDiscounted = isDiscounted,
    price = price.toScheduledPricingPrice(),
    from = from,
    to = to
)

internal fun PriceDto.toScheduledPricingPrice(): ScheduledPricing.Price = ScheduledPricing.Price(
    energyPricePerKWh = energyPricePerKWh,
    baseFee = baseFee,
    currency = currency,
    blockingFee = blockingFee?.toScheduledPricingBlockingFee()
)

internal fun BlockingFeeDto.toScheduledPricingBlockingFee(): ScheduledPricing.Price.BlockingFee =
    ScheduledPricing.Price.BlockingFee(
        pricePerMinute = pricePerMinute,
        startsAfterMinutes = startsAfterMinutes
    )
