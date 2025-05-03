package de.elvah.charge.features.adhoc_charging.ui.screens.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.adhoc_charging.ui.screens.review.model.PaymentSummaryUI
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration

internal class ReviewViewModel(
    private val getPaymentSummary: GetPaymentSummary,
    private val getOrganisationDetails: GetOrganisationDetails,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow<ReviewState>(ReviewState.Loading)
    val state: StateFlow<ReviewState> = _state.asStateFlow()

    init {
        val route = savedStateHandle.toRoute<AdHocChargingScreens.ReviewRoute>()

        viewModelScope.launch {
            val summary = async { getPaymentSummary(route.paymentId) }
            val organisationDetails = async { getOrganisationDetails() }

            summary.await().fold(
                ifLeft = {
                    _state.value = ReviewState.Error
                },
                ifRight = {
                    val duration = Duration.parse(it.totalTime)
                    _state.value = ReviewState.Success(
                        PaymentSummaryUI(
                            evseId = it.evseId,
                            cpoName = it.cpoName,
                            address = it.address,
                            totalTime = "${duration.inWholeMinutes}m ${duration.inWholeSeconds % 60}s",
                            consumedKWh = it.consumedKWh,
                            cpoLogo = organisationDetails.await()?.logoUrl.orEmpty(),
                            totalCost = it.totalCost.toDouble()/100
                        )
                    )
                }
            )
        }
    }
}
