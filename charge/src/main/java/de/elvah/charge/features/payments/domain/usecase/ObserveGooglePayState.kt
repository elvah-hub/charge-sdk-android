package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.payments.domain.manager.GooglePayManager
import de.elvah.charge.features.payments.domain.model.GooglePayState
import kotlinx.coroutines.flow.StateFlow

internal class ObserveGooglePayState(
    private val googlePayManager: GooglePayManager
) {
    operator fun invoke(): StateFlow<GooglePayState> = googlePayManager.state
}
