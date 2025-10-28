package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.ui.screens.activecharging.ActiveChargingViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart.ChargingStartViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.help.HelpViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.review.ReviewViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailViewModel
import de.elvah.charge.public_api.sitessource.SitesSource
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val adHocViewModelModule = module {
    viewModel { (source: SitesSource) ->
        SiteDetailViewModel(
            source,
            get(),
            get(),
            get(),
            get(),
        )
    }

    viewModelOf(::ChargingPointDetailViewModel)
    viewModelOf(::ChargingStartViewModel)
    viewModelOf(::ActiveChargingViewModel)
    viewModelOf(::HelpViewModel)
    viewModelOf(::ReviewViewModel)
}
