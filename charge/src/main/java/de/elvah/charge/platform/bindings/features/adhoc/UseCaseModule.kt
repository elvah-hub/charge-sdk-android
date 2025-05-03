package de.elvah.charge.platform.bindings.features.adhoc

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetPaymentToken

internal class UseCaseModule {



    fun providesGetOrganisationDetails(
        paymentsRepository: PaymentsRepository,
    ): GetOrganisationDetails = GetOrganisationDetails(
        paymentsRepository,
    )



    fun providesGetPaymentSummary(
        paymentsRepository: PaymentsRepository,
    ): GetPaymentSummary = GetPaymentSummary(paymentsRepository)



    fun providesObserveChargingSession(
        chargingRepository: ChargingRepository,
    ): ObserveChargingSession = ObserveChargingSession(chargingRepository)



    fun providesStartChargingSession(
        chargingRepository: ChargingRepository,
    ): StartChargingSession = StartChargingSession(chargingRepository)



    fun providesStopChargingSession(
        chargingRepository: ChargingRepository,
    ): StopChargingSession = StopChargingSession(chargingRepository)



    fun provideFetchChargingSession(
        chargingRepository: ChargingRepository,
    ): FetchChargingSession = FetchChargingSession(chargingRepository)



    fun providesGetPaymentToken(
        paymentsRepository: PaymentsRepository,
    ): GetPaymentToken = GetPaymentToken(paymentsRepository)



    fun providesHasActiveChargingSession(
        chargingRepository: ChargingRepository,
    ): HasActiveChargingSession = HasActiveChargingSession(chargingRepository)
}
