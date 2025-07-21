package de.elvah.charge.features.sites.ui.mapper

import de.elvah.charge.features.deals.ui.model.ChargePointUI
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI


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
