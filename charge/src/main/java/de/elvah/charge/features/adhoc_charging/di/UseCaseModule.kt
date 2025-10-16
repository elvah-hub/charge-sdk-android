package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.domain.usecase.ClearLocalSessionData
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.GetActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


internal val adHocChargingUseCasesModule = module {
    factoryOf(::ClearLocalSessionData)
    factoryOf(::FetchChargingSession)
    factoryOf(::GetActiveChargingSession)
    factoryOf(::HasActiveChargingSession)
    factoryOf(::ObserveChargingSession)
    factoryOf(::StartChargingSession)
    factoryOf(::StopChargingSession)
}
