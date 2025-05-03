package de.elvah.charge.features.deals.ui.mapper

import de.elvah.charge.features.deals.domain.model.ChargePoint
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.deals.ui.model.ChargePointUI
import de.elvah.charge.features.deals.ui.model.DealUI

internal fun Deal.toUI() = DealUI(
    id = this.id,
    cpoName = this.operatorName,
    pricePerKw = this.pricePerKwh,
    campaignEnd = this.campaignEnd,
    address = this.address,
    lat = this.lat,
    lng = this.lng,
    chargePoints = this.chargePoints.map { it.toUI() }
)

internal fun ChargePoint.toUI() = ChargePointUI(
    evseId = this.evseId,
    pricePerKwh = this.pricePerKwh,
    energyType = this.energyType,
    energyValue = this.energyValue,
    signedDeal = this.signedDeal
)