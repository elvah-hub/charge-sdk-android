package de.elvah.charge.features.payments.data.mapper

import de.elvah.charge.features.payments.data.remote.model.response.PaymentSummaryDto
import de.elvah.charge.features.payments.domain.model.PaymentSummary

internal fun PaymentSummaryDto.toDomain(cpoName: String): PaymentSummary {
    return PaymentSummary(
        evseId = this.data.address.locality,
        cpoName = cpoName,
        address = this.data.address.streetAddress,
        totalTime = this.data.totalTime,
        consumedKWh = this.data.consumedKWh,
        totalCost = this.data.totalCost.amount,
    )
}