package de.elvah.charge.features.sites.domain.usecase

import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.ui.utils.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class FindBestSite {

    operator fun invoke(
        sites: List<ChargeSite>,
    ): ChargeSite? {
        return sites
            .takeIf { it.isNotEmpty() }
            ?.filterEndedCampaign()
            ?.firstOrNull()
    }

    @OptIn(ExperimentalTime::class)
    private fun List<ChargeSite>.filterEndedCampaign(
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): List<ChargeSite> {
        return this.filter { site ->
            site.evses.any { cp ->
                cp.offer.campaignEndsAt
                    ?.toLocalDateTime(timeZone)
                    ?.toInstant(timeZone)
                    ?.let { campaignEndsAtInstant ->
                        campaignEndsAtInstant > Clock.System.now()
                    }
                    ?: false
            }
        }
    }
}
