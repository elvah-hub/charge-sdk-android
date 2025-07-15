package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.data.remote.api.ChargingApi
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.retrofit.RetrofitFactory
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val adHocChargingUseCasesModule = module {
    singleOf(::FetchChargingSession)
    singleOf(::HasActiveChargingSession)
    singleOf(::ObserveChargingSession)
    singleOf(::StartChargingSession)
    singleOf(::StopChargingSession)
}

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
