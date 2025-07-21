package de.elvah.charge.features.sites.di

import de.elvah.charge.features.adhoc_charging.data.local.DefaultChargingStore
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.deals.data.DefaultDealsRepository
import de.elvah.charge.features.deals.data.DefaultLocationRepository
import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.features.deals.domain.repository.LocationRepository
import de.elvah.charge.features.payments.data.repository.DefaultPaymentsRepository
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.sites.data.DefaultFiltersRepository
import de.elvah.charge.features.sites.data.DefaultSitesRepository
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sitesRepositoriesModule = module {
    singleOf(::DefaultFiltersRepository) { bind<FiltersRepository>() }
    singleOf(::DefaultSitesRepository) { bind<SitesRepository>() }
}
