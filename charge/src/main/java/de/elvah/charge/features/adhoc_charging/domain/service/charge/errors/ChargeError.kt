package de.elvah.charge.features.adhoc_charging.domain.service.charge.errors

import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions


internal sealed interface ChargeError {
    data class StartAttemptFailed(
        val error: SessionExceptions,
    ) : ChargeError

    data class StopAttemptFailed(
        val error: SessionExceptions,
    ) : ChargeError

    data class SessionCheckFailed(
        val error: Throwable,
    ) : ChargeError

    data object StartAttemptButSessionAlreadyExists : ChargeError

    data class SummaryRetryFailed(
        val error: Throwable,
    ) : ChargeError

    data object SummaryRetryFailedAllAttempts : ChargeError

    data class SessionPollingFailed(
        val error: Throwable,
    ) : ChargeError

    data object GenericError : ChargeError
}
