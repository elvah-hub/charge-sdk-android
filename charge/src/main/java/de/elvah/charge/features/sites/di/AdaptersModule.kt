package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.data.remote.model.adapter.OfferTypeAdapter
import de.elvah.charge.features.sites.data.remote.model.response.site.OfferTypeDto
import de.elvah.charge.platform.network.retrofit.model.AdapterHolder
import org.koin.dsl.module

private fun provideOfferTypeAdapter() = AdapterHolder(
    type = OfferTypeDto::class.java,
    jsonAdapter = OfferTypeAdapter(),
)

internal val adaptersModule = module {
    single { provideOfferTypeAdapter() }
}
