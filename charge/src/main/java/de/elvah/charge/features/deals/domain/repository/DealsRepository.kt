package de.elvah.charge.features.deals.domain.repository

import arrow.core.Either
import de.elvah.charge.features.deals.domain.model.Deal

internal interface DealsRepository {

    fun getDeal(dealId: String): Deal

    suspend fun getDeals(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
    ): Either<Exception, List<Deal>>
}