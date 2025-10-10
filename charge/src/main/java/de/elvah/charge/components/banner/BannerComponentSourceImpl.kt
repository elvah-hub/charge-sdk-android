package de.elvah.charge.components.banner

import de.elvah.charge.components.sitessource.InternalSitesSource
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
) : BannerComponentSource {

    private val internalSource = sitesSource as InternalSitesSource

    override val state = combine(
        internalSource.sites,
        sitesSource.activeSession,
    ) { sites, chargeSession ->
        when {
            chargeSession != null -> {
                SitesState.ActiveSession(
                    site = ChargeBannerActiveSessionRender(
                        id = chargeSession.evseId,
                        chargeTime = chargeSession.duration.seconds,
                    ),
                )
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
        initialValue = SitesState.Loading
    )
}
