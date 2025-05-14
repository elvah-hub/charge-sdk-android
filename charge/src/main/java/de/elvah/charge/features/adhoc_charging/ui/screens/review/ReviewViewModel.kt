package de.elvah.charge.features.adhoc_charging.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.ui.screens.review.model.PaymentSummaryUI
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetSessionDetails
import de.elvah.charge.features.payments.domain.usecase.ResetSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration

internal class ReviewViewModel(
    private val getPaymentSummary: GetPaymentSummary,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val resetSession: ResetSession,
    private val getSessionDetails: GetSessionDetails,
) : ViewModel() {

    private val _state = MutableStateFlow<ReviewState>(ReviewState.Loading)
    val state: StateFlow<ReviewState> = _state.asStateFlow()

    init {

        viewModelScope.launch {
            val sessioDetails = withContext(Dispatchers.IO) {
                getSessionDetails()
            }
            val summary = async { getPaymentSummary(sessioDetails.paymentId) }
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
                            totalCost = it.totalCost.toDouble() / 100
                        )
                    )
                }
            )

            resetSession()
        }
    }
}
