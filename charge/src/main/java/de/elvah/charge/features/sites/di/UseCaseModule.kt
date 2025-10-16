package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.GetBestSite
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.domain.usecase.GetSites
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import de.elvah.charge.features.sites.domain.usecase.UpdateSite
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


internal val sitesUseCaseModule = module {
    factory { GetFilters(get()) }
    factory { GetBestSite(get(), Dispatchers.Default) }
    factory { GetSite(get()) }
    factory { GetSites(get()) }
    factory { GetSiteScheduledPricing(get()) }
    factory { UpdateFilters(get()) }
    factory { UpdateSite(get()) }
    factory { ClearFilters(get()) }
}
