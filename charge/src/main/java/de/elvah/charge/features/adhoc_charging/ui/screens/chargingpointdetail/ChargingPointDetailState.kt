package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.model.ChargePointDetail
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.platform.core.mvi.Reducer

sealed class ChargingPointDetailState(open val evseId: String) : Reducer.ViewState {
    class Loading(evseId: String) : ChargingPointDetailState(evseId)
    class Error(evseId: String, val message: String) : ChargingPointDetailState(evseId)
    internal data class Success(
        override val evseId: String,
        val chargePointDetail: ChargePointDetail,
        val render: ChargePointDetailRender,
        val paymentIntentParams: PaymentConfiguration,
        val logoUrl: String,
    ) : ChargingPointDetailState(evseId)
}

data class ChargePointDetailRender(
    val evseId: EvseId,
    val energyType: String,
    val energyValue: Int,
    val price: Double,
    val originalPrice: Double?,
    val logoUrl: String,
    val cpoName: String,
    val termsUrl: String,
    val privacyUrl: String,
)
