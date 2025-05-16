package de.elvah.charge.features.payments.data.mapper

import de.elvah.charge.features.payments.data.remote.model.response.PaymentSummaryDto
import de.elvah.charge.features.payments.domain.model.PaymentSummary

internal fun PaymentSummaryDto.toDomain(evseId: String, cpoName: String): PaymentSummary {
    return PaymentSummary(
        evseId = evseId,
        cpoName = cpoName,
        address = this.data.address.streetAddress,
        totalTime = this.data.totalTimeInSeconds,
        consumedKWh = this.data.consumedKWh,
        totalCost = this.data.totalCost.amount,
    )
}