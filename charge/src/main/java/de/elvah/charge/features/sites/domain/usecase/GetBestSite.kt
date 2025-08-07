package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.sites.domain.exceptions.EmptyResultsException
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


internal class GetBestSite(
    private val getSites: GetSites,
    private val coroutineDispatcher: CoroutineDispatcher,
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(params: Params): Either<Throwable, ChargeSite> {
        return getSites(
            with(params) {
                GetSites.Params(
                    boundingBox = boundingBox,
                    campaignId = campaignId,
                    organisationId = organisationId,
                    offerType = offerType,
                    evseIds = evseIds
                )
            }
        ).flatMap {
            withContext(coroutineDispatcher) {
                if (it.isNotEmpty()) {
                    it.filterEndedCampaign().firstOrNull()?.right() ?: emptyResult

                } else {
                    emptyResult
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun List<ChargeSite>.filterEndedCampaign(): List<ChargeSite> {
        return this.filter { site ->
            site.evses.any {
                it.offer.campaignEndsAt?.let { campaignEndsAt ->
                    Instant.parse(campaignEndsAt) > Clock.System.now()
                } ?: false
            }
        }
    }

    private val emptyResult = EmptyResultsException().left()

    internal class Params(
        val boundingBox: BoundingBox? = null,
        val campaignId: String? = null,
        val organisationId: String? = null,
        val offerType: OfferType? = null,
        val evseIds: List<EvseId>? = null,
    )
}

