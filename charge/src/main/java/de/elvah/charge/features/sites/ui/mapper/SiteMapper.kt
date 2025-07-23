package de.elvah.charge.features.sites.ui.mapper

import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.model.Location


internal fun ChargeSite.toUI(): ChargeSiteUI =
    ChargeSiteUI(
        id = id,
        cpoName = operatorName,
        address = address.streetAddress.joinToString { " " },
        lat = location.first(),
        lng = location.last(),
        pricePerKw = evses.first().offer.price.energyPricePerKWh,
        campaignEnd = evses.first().offer.expiresAt,
        chargePoints = evses.map { it.toUI() }
    )

internal fun ChargeSite.ChargePoint.toUI(): ChargePointUI = ChargePointUI(
    evseId = evseId,
    pricePerKwh = offer.price.energyPricePerKWh,
    energyType = powerSpecification.type,
    energyValue = powerSpecification.maxPowerInKW,
)

internal fun ChargeSite.toRender(): ChargeBannerRender {
    val bestOffer = getBestOffer()

    return ChargeBannerRender(
        id = id,
        cpoName = operatorName,
        address = address.streetAddress.joinToString { " " },
        location = Location(
            location.first(),
            location.last()
        ),
        originalPrice = bestOffer.originalPrice?.energyPricePerKWh,
        price = bestOffer.price.energyPricePerKWh,
        campaignEnd = evses.first().offer.expiresAt,
    )
}

internal fun ChargeSite.getBestOffer(): ChargeSite.ChargePoint.Offer =
    this.evses.map { it to it.offer.price.energyPricePerKWh }.reduce { acc, pair ->
        minOf(acc, pair, compareBy { it.second })
    }.first.offer

