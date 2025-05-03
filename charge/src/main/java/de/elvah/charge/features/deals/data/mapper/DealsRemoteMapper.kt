package de.elvah.charge.features.deals.data.mapper

import de.elvah.charge.features.deals.data.remote.model.CPOItem
import de.elvah.charge.features.deals.data.remote.model.DealsDto
import de.elvah.charge.features.deals.data.remote.model.DealsItem
import de.elvah.charge.features.deals.domain.model.ChargePoint
import de.elvah.charge.features.deals.domain.model.Deal

internal fun DealsDto.toDomain(): List<Deal> {
    return this.data.map { it.toDomain() }
}

internal fun CPOItem.toDomain(): Deal {
    return Deal(
        id = this.id,
        operatorName = this.operatorName,
        pricePerKwh = this.deals.first().pricePerKWh.toDouble(),
        campaignEnd = this.deals.first().campaignEndDate,
        address = this.address.streetAddress.joinToString(" "),
        lat = this.location.first(),
        lng = this.location.get(1),
        chargePoints = this.deals.map { it.toDomain() }
    )
}

internal fun DealsItem.toDomain(): ChargePoint {
    return ChargePoint(
        evseId = this.evseId,
        pricePerKwh = this.pricePerKWh,
        energyType = this.powerSpecification.type,
        energyValue = this.powerSpecification.maxPowerInKW,
        signedDeal = this.signedDeal
    )
}