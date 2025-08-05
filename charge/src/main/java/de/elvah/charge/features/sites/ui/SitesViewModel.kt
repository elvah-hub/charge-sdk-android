package de.elvah.charge.features.sites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.GetActiveChargingSession
import de.elvah.charge.features.sites.domain.usecase.EmptyResultsException
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.mapper.toRender
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

internal class SitesViewModel(
    private val getSite: GetSite,
    private val getActiveChargingSession: GetActiveChargingSession,
    getFilters: GetFilters,
) : ViewModel() {

    val state = getFilters()
        .map {
            getSite(
                GetSite.Params(
                    boundingBox = it.boundingBox,
                    campaignId = it.campaignId,
                    organisationId = it.organisationId,
                    offerType = it.offerType
                )
            )
        }
        .map {
            val activeSession = getActiveChargingSession().getOrNull()

            if (activeSession != null) {
                SitesState.ActiveSession(
                    ChargeBannerActiveSessionRender(
                        activeSession.evseId, activeSession.duration.seconds
                    )
                )
            } else {
                it.getOrNull()?.let {
                    SitesState.Success(
                        it.toRender()
                    )
                } ?: run {
                    parseException(it.leftOrNull())
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SitesState.Loading
        )


    private fun parseException(exception: Throwable?): SitesState {
        return when (exception) {
            is EmptyResultsException -> {
                SitesState.Empty
            }

            else -> {
                SitesState.Error
            }
        }
    }

}
