package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.data.remote.SitesApi
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.retrofit.RetrofitFactory

internal fun provideApi(
    apiUrlBuilder: ApiUrlBuilder,
    retrofitFactory: RetrofitFactory,
): SitesApi {
    val baseUrl = apiUrlBuilder.url(
        serviceName = API_SERVICE_NAME,
    )

    return retrofitFactory
        .retrofit(
            baseUrl = baseUrl,
            service = SitesApi::class.java,
        )
}

private const val API_SERVICE_NAME = "integrate"
