package de.elvah.charge.platform.simulator.di

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.platform.simulator.data.repository.FakeChargingRepository
import de.elvah.charge.platform.simulator.data.repository.FakePaymentsRepository
import de.elvah.charge.platform.simulator.data.repository.FakeSitesRepository
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.factory.DefaultChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.factory.DefaultSimulationStrategyFactory
import de.elvah.charge.platform.simulator.domain.factory.SimulationStrategyFactory
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import de.elvah.charge.platform.simulator.domain.usecase.FakeInitStripeConfig
import de.elvah.charge.platform.simulator.ui.SimulatorListViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


fun provideSimulatorModule(simulatorFlow: SimulatorFlow) = module {
    viewModelOf(::SimulatorListViewModel)
    single {
        simulatorFlow
    }
    singleOf(::FakeSitesRepository) { bind<SitesRepository>() }
    singleOf(::FakePaymentsRepository) { bind<PaymentsRepository>() }
    singleOf(::DefaultChargingSessionFactory) { bind<ChargingSessionFactory>() }
    singleOf(::DefaultSimulationStrategyFactory) { bind<SimulationStrategyFactory>() }
    singleOf(::FakeChargingRepository) { bind<ChargingRepository>() }
    singleOf(::FakeInitStripeConfig) { bind<InitStripeConfig>() }


}
