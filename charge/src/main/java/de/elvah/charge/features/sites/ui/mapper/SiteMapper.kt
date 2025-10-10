package de.elvah.charge.features.sites.ui.mapper

import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.Offer
import de.elvah.charge.features.sites.ui.model.AddressUI
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.model.Location
import de.elvah.charge.public_api.model.EvseId

internal fun ChargeSite.toUI(): ChargeSiteUI {
    val commonPrefix = getCommonPrefixes()
    return ChargeSiteUI(
        id = id,
        cpoName = operatorName,
        address = address.toUI(),
        lat = location.first(),
        lng = location.last(),
        pricePerKw = evses.first().offer.price.energyPricePerKWh.value,
        campaignEnd = evses.first().offer.campaignEndsAt.orEmpty(),
        chargePoints = evses.map { it.toUI(commonPrefix) }
    )
}

internal fun ChargeSite.Address.toUI(): AddressUI =
    AddressUI(
        streetAddress = streetAddress,
        postalCode = postalCode,
        locality = locality,
    )


internal fun ChargeSite.ChargePoint.toUI(commonPrefix: String): ChargePointUI = ChargePointUI(
    evseId = EvseId(evseId),
    shortenedEvseId = evseId.removePrefix(commonPrefix),
    availability = availability,
    standardPricePerKwh = offer.price.energyPricePerKWh,
    maxPowerInKW = powerSpecification?.maxPowerInKW,
    powerType = powerSpecification?.type,
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
        originalPrice = bestOffer.originalPrice?.energyPricePerKWh?.value,
        price = bestOffer.price.energyPricePerKWh.value,
        campaignEnd = evses.first().offer.campaignEndsAt.orEmpty(),
    )
}

internal fun ChargeSite.getBestOffer(): Offer =
    this.evses.map { it to it.offer.price.energyPricePerKWh.value }.reduce { acc, pair ->
        minOf(acc, pair, compareBy { it.second })
    }.first.offer

private fun ChargeSite.getCommonPrefixes(
): String {
    val ids = evses.map { it.evseId }
    val commonPrefix = ids
        .takeIf { it.isNotEmpty() }
        ?.reduce { accumulator, nextElement ->
            accumulator.commonPrefixWith(nextElement, true)
        }
        ?: ""
    return commonPrefix
}
