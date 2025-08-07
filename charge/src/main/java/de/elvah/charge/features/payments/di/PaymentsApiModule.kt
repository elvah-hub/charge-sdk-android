package de.elvah.charge.features.payments.di

import de.elvah.charge.features.payments.data.remote.api.ChargeSettlementApi
import de.elvah.charge.features.payments.data.remote.api.IntegrateApi
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.retrofit.RetrofitFactory

internal fun provideChargeSettlementApi(
    apiUrlBuilder: ApiUrlBuilder,
    retrofitFactory: RetrofitFactory,
): ChargeSettlementApi {
    val baseUrl = apiUrlBuilder.url(
        serviceName = CHARGE_SETTLEMENT_API_SERVICE_NAME,
    )

    return retrofitFactory
        .retrofit(
            baseUrl,
            ChargeSettlementApi::class.java,
        )
}

internal fun provideIntegrateApi(
    apiUrlBuilder: ApiUrlBuilder,
    retrofitFactory: RetrofitFactory,
): IntegrateApi {
    val baseUrl = apiUrlBuilder.url(
        serviceName = CHARGE_SETTLEMENT_API_SERVICE_NAME,
    )

    return retrofitFactory
        .retrofit(
            baseUrl,
            IntegrateApi::class.java,
        )
}

private const val CHARGE_SETTLEMENT_API_SERVICE_NAME = "integrate"
