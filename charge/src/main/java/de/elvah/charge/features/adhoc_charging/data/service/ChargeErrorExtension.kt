package de.elvah.charge.features.adhoc_charging.data.service

import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError

internal fun Throwable.toChargeError(): ChargeError {
    // TODO: identify here all possible errors we want to share
    this
    return ChargeError.GENERIC_ERROR
}

internal fun SessionExceptions.toChargeError(): ChargeError {
    // TODO: identify here all possible errors we want to share
    return when (this) {
        SessionExceptions.OngoingSession -> ChargeError.START_ATTEMPT_BUT_SESSION_ALREADY_EXISTS
        SessionExceptions.GenericError -> ChargeError.GENERIC_ERROR
    }
}
