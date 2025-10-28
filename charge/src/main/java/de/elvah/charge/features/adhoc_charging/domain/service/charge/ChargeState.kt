package de.elvah.charge.features.adhoc_charging.domain.service.charge

internal enum class ChargeState {
    IDLE,
    VERIFYING,
    STARTING,
    STARTED,
    STOPPING,
    SUMMARY,
}
