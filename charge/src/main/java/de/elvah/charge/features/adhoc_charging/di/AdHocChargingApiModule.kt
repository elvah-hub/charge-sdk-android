package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.data.remote.api.ChargingApi
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.retrofit.RetrofitFactory

internal fun provideChargingApi(
    apiUrlBuilder: ApiUrlBuilder,
    retrofitFactory: RetrofitFactory,
): ChargingApi {
    val baseUrl = apiUrlBuilder.url(
        serviceName = API_SERVICE_NAME,
    )

    return retrofitFactory
        .retrofit(
            baseUrl,
            ChargingApi::class.java,
        )
}

private const val API_SERVICE_NAME = "direct-charge.backend"
