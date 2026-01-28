package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.PaymentConfigErrors
import de.elvah.charge.platform.core.mvi.Reducer

internal sealed class ChargingPointDetailEvent : Reducer.ViewEvent {
    data class Initialize(
        val paymentConfiguration: PaymentConfiguration,
        val logoUrl: String,
    ) : ChargingPointDetailEvent()

    data object OnGooglePayClicked : ChargingPointDetailEvent()
    data object OnPayWithCardClicked : ChargingPointDetailEvent()
    data class OnPaymentSuccess(val shortenedEvseId: String, val paymentId: String) :
        ChargingPointDetailEvent()

    data class OnError(val paymentConfigErrors: PaymentConfigErrors) : ChargingPointDetailEvent()

    data class OnGooglePayAvailabilityChanged(val isAvailable: Boolean) : ChargingPointDetailEvent()
}
