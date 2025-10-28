package de.elvah.charge.features.adhoc_charging.domain.service.charge.errors

internal enum class ChargeError {
    START_ATTEMPT_BUT_SESSION_ALREADY_EXISTS,
    SUMMARY_FAILED_ALL_ATTEMPTS,
    GENERIC_ERROR,
}
