package de.elvah.charge.features.adhoc_charging.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.ui.screens.review.model.PaymentSummaryUI
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetSummaryInfo
import de.elvah.charge.features.payments.domain.usecase.ResetSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

internal class ReviewViewModel(
    private val getPaymentSummary: GetPaymentSummary,
    private val resetSession: ResetSession,
    getSessionDetails: GetSummaryInfo,
) : ViewModel() {

    val state = getSessionDetails().map {
        Pair(it, getPaymentSummary(it.paymentId))
    }.map { (orgDetails, summary) ->
        summary.fold(
            ifLeft = { ReviewState.Error },
            ifRight = {
                val minutes = it.totalTime / 60
                val seconds = it.totalTime % 60

                ReviewState.Success(
                    PaymentSummaryUI(
                        evseId = it.evseId,
                        cpoName = it.cpoName,
                        address = it.address,
                        totalTime = "${minutes}m ${seconds}s",
                        consumedKWh = it.consumedKWh,
                        cpoLogo = orgDetails.logoUrl,
                        totalCost = it.totalCost.toDouble() / 100
                    )
                )
            }
        )
    }.onEach {
        resetSession()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReviewState.Loading)
}
