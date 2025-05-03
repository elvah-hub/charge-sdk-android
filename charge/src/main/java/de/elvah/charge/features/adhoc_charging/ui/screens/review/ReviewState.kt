package de.elvah.charge.features.adhoc_charging.ui.screens.review

import de.elvah.charge.features.adhoc_charging.ui.screens.review.model.PaymentSummaryUI

internal sealed class ReviewState {
    data object Loading : ReviewState()
    data object Error : ReviewState()
    data class Success(val summary: PaymentSummaryUI) : ReviewState()
}