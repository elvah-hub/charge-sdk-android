package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import de.elvah.charge.features.adhoc_charging.ui.model.AdditionalCostsUI
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.PaymentConfigErrors
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.config.PaymentConfig
import de.elvah.charge.platform.core.mvi.Reducer
import kotlinx.datetime.LocalDateTime

internal sealed class ChargingPointDetailState(
    open val evseId: String,
) : Reducer.ViewState {

    class Loading(evseId: String) : ChargingPointDetailState(evseId)

    class Error(evseId: String, val paymentConfigErrors: PaymentConfigErrors) :
        ChargingPointDetailState(evseId)

    internal data class Success(
        override val evseId: String,
        val shortenedEvseId: String,
        val availability: ChargePointAvailability,
        val discountExpiresAt: LocalDateTime?,
        val priceWithLineThrough: Pricing?,
        val priceToHighlight: Pricing,
        val additionalCostsUI: AdditionalCostsUI?,
        val companyName: String,
        val termsOfServiceUrl: String,
        val privacyPolicyUrl: String,
        val companyLogoUrl: String?,
        val paymentIntentParams: PaymentConfiguration,
        val paymentConfig: PaymentConfig,
        val environment: Environment,
        val mocked: Boolean = false,
    ) : ChargingPointDetailState(evseId)
}
