package de.elvah.charge.features.deals.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.deals.domain.repository.DealsRepository


internal class GetDeals(private val dealsRepository: DealsRepository) {
    suspend operator fun invoke(params: Params): Either<Exception, List<Deal>> {
        return either {
            val deals = dealsRepository.getDeals(
                minLat = params.minLat,
                maxLat = params.maxLat,
                minLng = params.minLng,
                maxLng = params.maxLng
            )

            deals.bind()
        }
    }

    internal class Params(
        val minLat: Double,
        val minLng: Double,
        val maxLat: Double,
        val maxLng: Double,
    )
}
