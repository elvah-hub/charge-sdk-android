package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ChargingPointDetailRoute
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Error
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Loading
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Success
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.model.ChargePointDetail
import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfiguration
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import de.elvah.charge.platform.core.mvi.MVIBaseViewModel
import de.elvah.charge.platform.core.mvi.Reducer
import kotlinx.coroutines.launch

internal class ChargingPointDetailViewModel(
    private val getPaymentConfiguration: GetPaymentConfiguration,
    private val initStripeConfig: InitStripeConfig,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val dealsRepository: DealsRepository,
    private val savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<ChargingPointDetailState, ChargingPointDetailEvent, ChargingPointDetailEffect>(
    initialState = Loading(savedStateHandle.toRoute<ChargingPointDetailRoute>().evseId),
    reducer = Reducer { previousState, event ->
        val evseId = savedStateHandle.toRoute<ChargingPointDetailRoute>().evseId

        when (event) {
            ChargingPointDetailEvent.OnGooglePayClicked -> when (previousState) {
                is Loading -> Reducer.Result(Loading(evseId), null)
                is Error -> Reducer.Result(Error(evseId, previousState.message), null)
                is Success -> Reducer.Result(previousState.copy(), null)
            }

            ChargingPointDetailEvent.OnPayWithCardClicked -> when (previousState) {
                is Loading -> Reducer.Result(Loading(evseId), null)
                is Error -> Reducer.Result(Error(evseId, previousState.message), null)
                is Success -> Reducer.Result(previousState.copy(), null)
            }

            is ChargingPointDetailEvent.Initialize -> {
                val route = savedStateHandle.toRoute<ChargingPointDetailRoute>()
                val deal = dealsRepository.getDeal(route.dealId)
                val chargePoint = deal.chargePoints.first { route.evseId == it.evseId }
                val organisationDetails = getOrganisationDetails()

                Reducer.Result(
                    Success(
                        evseId,
                        ChargePointDetail(
                            chargingPoint = chargePoint.evseId,
                            type = chargePoint.energyType,
                            price = ChargePointDetail.Price(
                                current = chargePoint.pricePerKwh.toString(),
                                old = chargePoint.pricePerKwh.toString()
                            ),
                            cpoName = deal.operatorName,
                            evseId = chargePoint.evseId,
                            energy = chargePoint.energyValue.toString(),
                            signedOffer = "",
                            termsUrl = organisationDetails?.termsOfConditionUrl.orEmpty(),
                            privacyUrl = organisationDetails?.privacyUrl.orEmpty()
                        ),
                        paymentIntentParams = event.paymentConfiguration,
                        logoUrl = event.logoUrl
                    ), null
                )
            }

            ChargingPointDetailEvent.OnPaymentSuccess -> {
                Reducer.Result(
                    previousState,
                    ChargingPointDetailEffect.NavigateTo(previousState.evseId)
                )
            }
        }
    }
) {
    init {
        viewModelScope.launch {
            val route = savedStateHandle.toRoute<ChargingPointDetailRoute>()
            executeInitializeStripe(route.signedOffer)
        }
    }

    private suspend fun executeInitializeStripe(evseId: String) {
        val result: Either<Exception, PaymentConfiguration> = getPaymentConfiguration(evseId)
        val logoUrl = getOrganisationDetails()?.logoUrl.orEmpty()

        result.fold(
            ifLeft = {
                Log.d("ChargingPointDetailViewModel", "Error getting payment configuration", it)
            }, ifRight = { paymentIntentValue ->
                initStripeConfig(paymentIntentValue.publishableKey, paymentIntentValue.accountId)
                sendEvent(ChargingPointDetailEvent.Initialize(paymentIntentValue, logoUrl))
            }
        )
    }
}

sealed class ChargingPointDetailEvent : Reducer.ViewEvent {
    internal data class Initialize(
        val paymentConfiguration: PaymentConfiguration,
        val logoUrl: String,
    ) : ChargingPointDetailEvent()

    data object OnGooglePayClicked : ChargingPointDetailEvent()
    data object OnPayWithCardClicked : ChargingPointDetailEvent()
    data object OnPaymentSuccess : ChargingPointDetailEvent()
}

sealed class ChargingPointDetailEffect : Reducer.ViewEffect {
    class NavigateTo(val evseId: String) : ChargingPointDetailEffect()
}