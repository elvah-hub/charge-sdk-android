package de.elvah.charge.features.deals.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.deals.data.mapper.toDomain
import de.elvah.charge.features.deals.data.remote.DealsApi
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither



internal class DefaultDealsRepository(
    private val dealsApi: DealsApi,
) : DealsRepository {

    private var deals: List<Deal> = emptyList()

    override fun getDeal(dealId: String): Deal {
        println("${hashCode()} Deals: $deals")
        return deals.first { it.id == dealId }
    }

    override suspend fun getDeals(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
    ): Either<Exception, List<Deal>> {
        return runCatching {
            dealsApi.getDeals(
                minLat = minLat,
                maxLat = maxLat,
                minLng = minLng,
                maxLng = maxLng
            ).data.map { it.toDomain() }.also {
                deals = it
            }
        }.toEither()
    }
}
