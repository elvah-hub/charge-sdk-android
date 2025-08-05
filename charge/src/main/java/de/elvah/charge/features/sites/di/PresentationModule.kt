package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.ui.SitesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sitesViewModelModule = module {
    viewModelOf(::SitesViewModel)
}
