package de.elvah.charge.features.deals.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.deals.domain.model.Deal


internal class GetDeal(
    private val getDeals: GetDeals,
) {
    suspend operator fun invoke(params: Params): Either<Exception, Deal> {
        return getDeals(
            with(params) {
                GetDeals.Params(
                    minLat = minLat,
                    minLng = minLng,
                    maxLat = maxLat,
                    maxLng = maxLng,
                )
            }
        ).map { it.first() }
    }

    internal class Params(
        val minLat: Double,
        val minLng: Double,
        val maxLat: Double,
        val maxLng: Double,
    )
}