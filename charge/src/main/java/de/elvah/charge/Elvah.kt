package de.elvah.charge

import android.content.Context
import de.elvah.charge.features.adhoc_charging.data.local.DefaultChargingStore
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
import de.elvah.charge.features.adhoc_charging.di.adHocChargingUseCasesModule
import de.elvah.charge.features.adhoc_charging.di.provideChargingApi
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.adhoc_charging.ui.screens.activecharging.ActiveChargingViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart.ChargingStartViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.help.HelpViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.review.ReviewViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailViewModel
import de.elvah.charge.features.deals.data.DefaultDealsRepository
import de.elvah.charge.features.deals.data.DefaultLocationRepository
import de.elvah.charge.features.deals.di.provideApi
import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.features.deals.domain.repository.LocationRepository
import de.elvah.charge.features.deals.domain.usecase.GetDeal
import de.elvah.charge.features.deals.domain.usecase.GetDeals
import de.elvah.charge.features.deals.domain.usecase.GetLocation
import de.elvah.charge.features.deals.domain.usecase.UpdateLocation
import de.elvah.charge.features.deals.ui.DealsViewModel
import de.elvah.charge.features.payments.data.repository.DefaultPaymentsRepository
import de.elvah.charge.features.payments.di.provideChargeSettlementApi
import de.elvah.charge.features.payments.di.provideIntegrateApi
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetPaymentToken
import de.elvah.charge.features.payments.domain.usecase.GetSessionDetails
import de.elvah.charge.features.payments.domain.usecase.GetSummaryInfo
import de.elvah.charge.features.payments.domain.usecase.ResetSession
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import de.elvah.charge.features.sites.di.sitesRepositoriesModule
import de.elvah.charge.features.sites.di.sitesUseCaseModule
import de.elvah.charge.features.sites.di.sitesViewModelModule
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.okhttp.di.okHttpModule
import de.elvah.charge.platform.network.retrofit.di.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object Elvah {

    private val useCaseModule = module {
        singleOf(::GetDeal)
        singleOf(::GetDeals)
        singleOf(::UpdateLocation)
        singleOf(::GetLocation)
        singleOf(::GetPaymentConfiguration)
        singleOf(::InitStripeConfig)
        singleOf(::GetOrganisationDetails)
        singleOf(::GetPaymentToken)
        singleOf(::GetPaymentSummary)
        singleOf(::ResetSession)
        singleOf(::GetSessionDetails)
        singleOf(::GetSummaryInfo)
    }

    private val viewModelsModule = module {
        viewModelOf(::DealsViewModel)
        viewModelOf(::SiteDetailViewModel)
        viewModelOf(::ChargingPointDetailViewModel)
        viewModelOf(::ChargingStartViewModel)
        viewModelOf(::ActiveChargingViewModel)
        viewModelOf(::HelpViewModel)
        viewModelOf(::ReviewViewModel)
    }

    val repositoriesModule = module {
        singleOf(::DefaultChargingRepository) { bind<ChargingRepository>() }
        singleOf(::DefaultPaymentsRepository) { bind<PaymentsRepository>() }
        singleOf(::DefaultChargingStore) { bind<ChargingStore>() }
        singleOf(::DefaultLocationRepository) { bind<LocationRepository>() }
        singleOf(::DefaultDealsRepository) { bind<DealsRepository>() }
    }

    val networkModule = module {
        singleOf(::ApiUrlBuilder)
        single {
            provideChargingApi(get(), get())
        }
        single {
            provideIntegrateApi(get(), get())
        }

        single {
            provideChargeSettlementApi(get(), get())
        }
        single { provideApi(get(), get()) }
        single { de.elvah.charge.features.sites.di.provideApi(get(), get()) }
    }

    val localModule = module {

    }

    fun initialize(context: Context, config: Config) {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(
                viewModelsModule,
                useCaseModule,
                networkModule,
                localModule,
                repositoriesModule,
                okHttpModule,
                retrofitModule,
                adHocChargingUseCasesModule,
                sitesRepositoriesModule,
                sitesUseCaseModule,
                sitesViewModelModule,
            )
        }

        ChargeConfig.initialize(
            de.elvah.charge.platform.config.Config(
                config.apiKey,
                config.darkTheme,
                config.environment
            )
        )
    }
}

data class Config(
    val apiKey: String,
    val darkTheme: Boolean? = null,
    val environment: String = "int",
)
