package de.elvah.charge.features.adhoc_charging.ui.screens.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.ui.screens.review.model.PaymentSummaryUI
import de.elvah.charge.features.payments.domain.usecase.GetChargeSessionSummary
import de.elvah.charge.features.payments.domain.usecase.ResetChargeSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class ReviewViewModel(
    getChargeSessionSummary: GetChargeSessionSummary,
    private val resetChargeSession: ResetChargeSession,
) : ViewModel() {

    val isLoading = MutableStateFlow(false)

    val state = combine(
        isLoading,
        getChargeSessionSummary(), /// TODO: how to retry if fails all x auto attempts?
    ) { loading, summary ->
        if (summary == null && !loading) return@combine ReviewState.Error

        val summaryUI = summary?.let {
            val minutes = summary.totalTime / 60
            val seconds = summary.totalTime % 60

            PaymentSummaryUI(
                evseId = summary.evseId,
                cpoName = summary.cpoName,
                address = summary.address,
                totalTime = "${minutes}m ${seconds}s",
                consumedKWh = summary.consumedKWh,
                cpoLogo = summary.logoUrl,
                totalCost = summary.totalCost.toDouble() / 100
            )
        }

        ReviewState.Success(
            summary = summaryUI,
            isLoading = loading,
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReviewState.Loading,
    )

    internal fun clearChargeSession() = resetChargeSession()
}
