package de.elvah.charge.features.sites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.GetActiveChargingSession
import de.elvah.charge.features.sites.domain.exceptions.EmptyResultsException
import de.elvah.charge.features.sites.domain.usecase.GetBestSite
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.mapper.toRender
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

internal class SitesViewModel(
    private val getBestSite: GetBestSite,
    private val getActiveChargingSession: GetActiveChargingSession,
    getFilters: GetFilters,
) : ViewModel() {

    val state = getFilters()
        .map {
            getBestSite(
                GetBestSite.Params(
                    boundingBox = it.boundingBox,
                    campaignId = it.campaignId?.value,
                    organisationId = it.organisationId?.value,
                    offerType = it.offerType,
                    evseIds = it.evseIds
                )
            )
        }
        .map { site ->
            val activeSession = getActiveChargingSession().getOrNull()
            if (activeSession != null) {
                SitesState.ActiveSession(
                    ChargeBannerActiveSessionRender(
                        activeSession.evseId, activeSession.duration.seconds
                    )
                )
            } else {
                site.getOrNull()?.let { site ->
                    SitesState.Success(
                        site.toRender()
                    )
                } ?: run {
                    parseException(site.leftOrNull())
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SitesState.Loading
        )


    private fun parseException(exception: Throwable?): SitesState = when (exception) {
        is EmptyResultsException -> {
            SitesState.Empty
        }

        else -> {
            SitesState.Error
        }
    }
}
