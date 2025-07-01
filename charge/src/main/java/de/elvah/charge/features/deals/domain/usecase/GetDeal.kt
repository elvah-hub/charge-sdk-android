package de.elvah.charge.features.deals.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
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
        ).flatMap {
            if (it.isNotEmpty()) {
                it.first().right()
            } else {
                Exception("No deals found").left()
            }
        }
    }

    internal class Params(
        val minLat: Double,
        val minLng: Double,
        val maxLat: Double,
        val maxLng: Double,
    )
}
