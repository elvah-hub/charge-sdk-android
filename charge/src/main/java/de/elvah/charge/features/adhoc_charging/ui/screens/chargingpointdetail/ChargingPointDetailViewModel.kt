package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import android.util.Log
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ChargingPointDetailRoute
import de.elvah.charge.features.adhoc_charging.ui.mapper.toUI
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailEffect.NavigateTo
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Error
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Loading
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailState.Success
import de.elvah.charge.features.payments.domain.manager.GooglePayManager
import de.elvah.charge.features.payments.domain.model.GooglePayState
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.PaymentConfigErrors
import de.elvah.charge.features.payments.domain.usecase.PaymentConfigErrors.NoOfferFound
import de.elvah.charge.features.payments.domain.usecase.StoreAdditionalCosts
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.ui.mapper.toUI
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.core.mvi.MVIBaseViewModel
import de.elvah.charge.platform.core.mvi.Reducer
import de.elvah.charge.platform.core.mvi.Reducer.Result
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class ChargingPointDetailViewModel(
    private val args: ChargingPointDetailRoute,
    private val getPaymentConfiguration: GetPaymentConfiguration,
    private val initStripeConfig: InitStripeConfig,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val storeAdditionalCosts: StoreAdditionalCosts,
    private val sitesRepository: SitesRepository,
    private val config: Config,
    private val googlePayManager: GooglePayManager,
) : MVIBaseViewModel<ChargingPointDetailState, ChargingPointDetailEvent, ChargingPointDetailEffect>(
    initialState = Loading(args.evseId),
    reducer = Reducer { previousState, event ->
        val evseId = args.evseId

        when (val event = event) {
            ChargingPointDetailEvent.OnGooglePayClicked -> when (previousState) {
                is Loading -> Result(Loading(evseId), null)
                is Error -> Result(Error(evseId, previousState.paymentConfigErrors), null)
                is Success -> Result(previousState.copy(), null)
            }

            ChargingPointDetailEvent.OnPayWithCardClicked -> when (previousState) {
                is Loading -> Result(Loading(evseId), null)
                is Error -> Result(Error(evseId, previousState.paymentConfigErrors), null)
                is Success -> Result(previousState.copy(), null)
            }

            is ChargingPointDetailEvent.Initialize -> {
                sitesRepository.getChargeSite(args.siteId)
                    .fold(
                        ifLeft = {
                            Result(
                                Error(
                                    evseId,
                                    NoOfferFound(it.cause)
                                )
                            )
                        },
                        ifRight = { site ->
                            val siteUI = site.toUI()

                            val chargePoint = site.evses
                                .first { args.evseId == it.evseId }

                            val chargePointUI = siteUI.chargePoints
                                .find { args.evseId == it.evseId.value }

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

                            Result(
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

            is ChargingPointDetailEvent.OnPaymentSuccess -> {
                Result(
                    previousState,
                    NavigateTo(event.shortenedEvseId, event.paymentId)
                )
            }

            is ChargingPointDetailEvent.OnError -> {
                Result(
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
            executeInitializeStripe(args.siteId, args.evseId)
        }

        viewModelScope.launch {
            googlePayManager.paymentState.collect { googlePayState ->
                when (googlePayState) {
                    is GooglePayState.Success -> {

                        (state.value as? Success)?.let {
                            sendEvent(
                                ChargingPointDetailEvent.OnPaymentSuccess(
                                    it.shortenedEvseId, it.paymentIntentParams.paymentId
                                ), true
                            )
                            googlePayManager.resetPaymentState()
                        }
                    }

                    is GooglePayState.Failed -> {
                        Log.e(
                            "ChargingPointDetailViewModel",
                            "Google Pay payment failed: ${googlePayState.error}"
                        )
                        // Handle Google Pay errors here if needed in the future
                    }

                    is GooglePayState.Cancelled -> {
                        Log.i(
                            "ChargingPointDetailViewModel",
                            "Google Pay payment cancelled by user"
                        )
                        // Handle Google Pay cancellation here if needed in the future
                    }

                    GooglePayState.Idle,
                    GooglePayState.Processing -> {
                        // No action needed for these states
                    }
                }
            }
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
            executeInitializeStripe(args.siteId, args.evseId)
        }
    }
}
