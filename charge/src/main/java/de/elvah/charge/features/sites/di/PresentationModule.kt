package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.ui.SitesViewModel
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val sitesViewModelModule = module {
    viewModelOf(::SitesViewModel)
    viewModelOf(::PricingGraphViewModel)
}
