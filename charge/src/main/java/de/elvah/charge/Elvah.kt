package de.elvah.charge

import android.content.Context
import de.elvah.charge.di.okHttpModule
import de.elvah.charge.di.retrofitModule
import de.elvah.charge.features.adhoc_charging.data.di.AdHocChargingApiModule
import de.elvah.charge.features.adhoc_charging.data.local.DefaultChargingStore
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.adhoc_charging.ui.screens.activecharging.ActiveChargingViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart.ChargingStartViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.help.HelpViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.review.ReviewViewModel
import de.elvah.charge.features.deals.data.DefaultDealsRepository
import de.elvah.charge.features.deals.data.DefaultLocationRepository
import de.elvah.charge.features.deals.data.di.DealsApiModule
import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.features.deals.domain.repository.LocationRepository
import de.elvah.charge.features.deals.domain.usecase.GetDeal
import de.elvah.charge.features.deals.domain.usecase.GetDeals
import de.elvah.charge.features.deals.domain.usecase.GetLocation
import de.elvah.charge.features.deals.domain.usecase.UpdateLocation
import de.elvah.charge.features.deals.ui.DealsViewModel
import de.elvah.charge.features.payments.data.di.PaymentsApiModule
import de.elvah.charge.features.payments.data.repository.DefaultPaymentsRepository
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetPaymentToken
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import de.elvah.charge.platform.bindings.features.adhoc.SharedPreferencesModule
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.network.ApiUrlBuilder
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
        singleOf(::HasActiveChargingSession)
        singleOf(::GetPaymentConfiguration)
        singleOf(::InitStripeConfig)
        singleOf(::GetOrganisationDetails)
        singleOf(::GetPaymentToken)
        singleOf(::GetPaymentSummary)
        singleOf(::ObserveChargingSession)
        singleOf(::StartChargingSession)
        singleOf(::StopChargingSession)
        singleOf(::FetchChargingSession)
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
            AdHocChargingApiModule.provideChargingApi(get(), get())
        }
        single {
            PaymentsApiModule.provideIntegrateApi(get(), get())
        }

        single {
            PaymentsApiModule.provideChargeSettlementApi(get(), get())
        }
        single { DealsApiModule.provideApi(get(), get()) }
    }

    val localModule = module {
        single {
            SharedPreferencesModule.providesSharedPreferences(get())
        }
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
                retrofitModule
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