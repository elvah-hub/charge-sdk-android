package de.elvah.charge.features.deals.data.di

import de.elvah.charge.features.deals.data.remote.DealsApi
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.retrofit.RetrofitFactory

internal object DealsApiModule {


    fun provideApi(
        apiUrlBuilder: ApiUrlBuilder,
        retrofitFactory: RetrofitFactory,
    ): DealsApi {
        val baseUrl = apiUrlBuilder.url(
            serviceName = API_SERVICE_NAME,
        )

        return retrofitFactory
            .retrofit(
                baseUrl,
                DealsApi::class.java,
            )
    }
}

private const val API_SERVICE_NAME = "discovery.backend"
