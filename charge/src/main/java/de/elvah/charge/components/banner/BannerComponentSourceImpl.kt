package de.elvah.charge.components.banner

import de.elvah.charge.components.sitessource.InternalSitesSource
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isChargingState
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isSummaryState
import de.elvah.charge.features.adhoc_charging.domain.usecase.GetChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailViewModel.ChargeIndicatorUI
import de.elvah.charge.features.sites.domain.usecase.FindBestSite
import de.elvah.charge.features.sites.ui.SitesState
import de.elvah.charge.features.sites.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.mapper.toRender
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

internal class BannerComponentSourceImpl(
    coroutineScope: CoroutineScope,
    sitesSource: SitesSource,
    private val findBestSite: FindBestSite,
    observeChargingState: ObserveChargingState,
    getChargingSession: GetChargingSession,
) : BannerComponentSource {

    private val internalSource = sitesSource as InternalSitesSource

    internal val chargeIndicator = combine(
        observeChargingState(),
        getChargingSession(),
    ) { state, session ->
        ChargeIndicatorUI(
            isCharging = session?.status?.isChargingState == true,
            isSummaryReady = state.isSummaryState,
            evseId = session?.evseId,
            chargeTime = session?.duration?.seconds
        )

    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChargeIndicatorUI(
            isCharging = getChargingSession().value?.status?.isChargingState == true,
            isSummaryReady = observeChargingState().value.isSummaryState,
            evseId = getChargingSession().value?.evseId,
            chargeTime = getChargingSession().value?.duration?.seconds,
        ),
    )

    override val state = combine(
        internalSource.sites,
        chargeIndicator,
    ) { sites, indicator ->
        when {
            indicator.isSummaryReady || indicator.isCharging -> {
                SitesState.ActiveSession(
                    site = ChargeBannerActiveSessionRender(
                        id = indicator.evseId.orEmpty(),
                        chargeTime = indicator.chargeTime ?: 0.seconds,
                        isSummaryReady = indicator.isSummaryReady,
                    ),
                )
            }

            internalSource.isIdle -> {
                SitesState.Idle
            }

            sites == null -> {
                SitesState.Error
            }

            else -> {
                findBestSite(sites)
                    ?.let { bestSite ->
                        SitesState.Success(
                            site = bestSite.toRender(),
                        )
                    }
                    ?: SitesState.Empty
            }
        }

    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SitesState.Loading,
    )
}
