package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import arrow.core.Either
import de.elvah.charge.components.sitessource.InternalSitesSource
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ChargingPointDetailRoute
import de.elvah.charge.features.adhoc_charging.ui.mapper.toUI
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Error
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Loading
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Success
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.PaymentConfigErrors
import de.elvah.charge.features.payments.domain.usecase.StoreAdditionalCosts
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import de.elvah.charge.features.sites.ui.mapper.toUI
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.core.mvi.MVIBaseViewModel
import de.elvah.charge.platform.core.mvi.Reducer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// TODO: display screen, set payment button with loading indicator while the payment signed offer is fetch.
//  show error screen if fails.
internal class ChargingPointDetailViewModel(
    private val internalSitesSource: InternalSitesSource,
    private val getPaymentConfiguration: GetPaymentConfiguration, // TODO: needs adjustment
    private val initStripeConfig: InitStripeConfig,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val storeAdditionalCosts: StoreAdditionalCosts,
    private val savedStateHandle: SavedStateHandle,
    private val config: Config,
) : MVIBaseViewModel<ChargingPointDetailState, ChargingPointDetailEvent, ChargingPointDetailEffect>(
    initialState = Loading(savedStateHandle.toRoute<ChargingPointDetailRoute>().evseId),
    reducer = Reducer { previousState, event ->
        val evseId = savedStateHandle.toRoute<ChargingPointDetailRoute>().evseId

        when (event) {
            ChargingPointDetailEvent.OnGooglePayClicked -> when (previousState) {
                is Loading -> Reducer.Result(Loading(evseId), null)
                is Error -> Reducer.Result(Error(evseId, previousState.paymentConfigErrors), null)
                is Success -> Reducer.Result(previousState.copy(), null)
            }

            ChargingPointDetailEvent.OnPayWithCardClicked -> when (previousState) {
                is Loading -> Reducer.Result(Loading(evseId), null)
                is Error -> Reducer.Result(Error(evseId, previousState.paymentConfigErrors), null)
                is Success -> Reducer.Result(previousState.copy(), null)
            }

            is ChargingPointDetailEvent.Initialize -> {
                val route = savedStateHandle.toRoute<ChargingPointDetailRoute>()

                internalSitesSource.getSite(route.siteId)
                    .fold(
                        ifLeft = {
                            Reducer.Result(
                                Error(
                                    evseId,
                                    PaymentConfigErrors.NoOfferFound(it.cause)
                                )
                            )
                        },
                        ifRight = { site ->
                            val siteUI = site.toUI()

                            val chargePoint = site.evses
                                .first { route.evseId == it.evseId }

                            val chargePointUI = siteUI.chargePoints
                                .find { route.evseId == it.evseId.value }

                            val currentPrice = chargePoint.offer.price

                            val standardPrice = chargePoint.offer.originalPrice
                                ?: currentPrice

                            val hasDiscount =
                                currentPrice.energyPricePerKWh.value < standardPrice.energyPricePerKWh.value

                            val priceWithLineThrough =
                                (if (hasDiscount) standardPrice else null)

                            val priceToHighlight =
                                (if (hasDiscount) currentPrice else standardPrice)


                            val additionalCosts = AdditionalCosts(
                                baseFee = priceToHighlight.baseFee,
                                blockingFee = priceToHighlight.blockingFee,
                                currency = priceToHighlight.currency,
                            )

                            runBlocking { storeAdditionalCosts(additionalCosts) }

                            val organisationDetails = runBlocking { getOrganisationDetails() }

                            Reducer.Result(
                                Success(
                                    evseId = evseId,
                                    shortenedEvseId = chargePointUI?.shortenedEvseId ?: evseId,
                                    availability = chargePoint.availability,
                                    discountExpiresAt = null, // disabled for now
                                    priceWithLineThrough = priceWithLineThrough?.energyPricePerKWh,
                                    priceToHighlight = priceToHighlight.energyPricePerKWh,
                                    additionalCostsUI = additionalCosts.toUI(),
                                    companyName = organisationDetails?.companyName.orEmpty(),
                                    termsOfServiceUrl = organisationDetails?.termsOfConditionUrl.orEmpty(),
                                    privacyPolicyUrl = organisationDetails?.privacyUrl.orEmpty(),
                                    companyLogoUrl = organisationDetails?.logoUrl,
                                    paymentIntentParams = event.paymentConfiguration,
                                    mocked = config.environment is Environment.Simulator,
                                ), null
                            )
                        })
            }

            ChargingPointDetailEvent.OnPaymentSuccess -> {
                Reducer.Result(
                    previousState,
                    ChargingPointDetailEffect.NavigateTo(previousState.evseId)
                )
            }

            is ChargingPointDetailEvent.OnError -> {
                Reducer.Result(
                    Error(
                        previousState.evseId,
                        event.paymentConfigErrors
                    ), null
                )
            }
        }
    }
) {
    init {
        viewModelScope.launch {
            val route = savedStateHandle.toRoute<ChargingPointDetailRoute>()
            executeInitializeStripe(route.siteId, route.evseId)
        }
    }

    private suspend fun executeInitializeStripe(siteId: String, evseId: String) {
        val result: Either<PaymentConfigErrors, PaymentConfiguration> =
            getPaymentConfiguration(siteId, evseId)
        val logoUrl = getOrganisationDetails()?.logoUrl.orEmpty()

        result.fold(
            ifLeft = {
                Log.d(
                    "ChargingPointDetailViewModel",
                    "Error getting payment configuration",
                    it.throwable?.cause
                )
                sendEvent(ChargingPointDetailEvent.OnError(it))
            }, ifRight = { paymentIntentValue ->
                initStripeConfig(paymentIntentValue.publishableKey, paymentIntentValue.accountId)
                sendEvent(ChargingPointDetailEvent.Initialize(paymentIntentValue, logoUrl))
            }
        )
    }

    internal fun onRetryClicked() {
        viewModelScope.launch {
            val route = savedStateHandle.toRoute<ChargingPointDetailRoute>()
            executeInitializeStripe(route.siteId, route.evseId)
        }
    }
}
