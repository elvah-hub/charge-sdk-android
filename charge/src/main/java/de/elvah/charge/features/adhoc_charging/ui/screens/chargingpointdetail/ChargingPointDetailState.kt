package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import de.elvah.charge.public_api.banner.EvseId
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.model.ChargePointDetail
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.platform.core.mvi.Reducer

internal sealed class ChargingPointDetailState(open val evseId: String) : Reducer.ViewState {
    class Loading(evseId: String) : ChargingPointDetailState(evseId)
    class Error(evseId: String, val message: String) : ChargingPointDetailState(evseId)
    internal data class Success(
        override val evseId: String,
        val chargePointDetail: ChargePointDetail,
        val render: ChargePointDetailRender,
        val paymentIntentParams: PaymentConfiguration,
        val logoUrl: String,
        val mocked: Boolean = false,
    ) : ChargingPointDetailState(evseId)
}

internal data class ChargePointDetailRender(
    val evseId: EvseId,
    val energyType: String,
    val energyValue: Float?,
    val price: Double,
    val originalPrice: Double?,
    val logoUrl: String,
    val cpoName: String,
    val termsUrl: String,
    val privacyUrl: String,
)
