package de.elvah.charge.features.adhoc_charging.data.repository

internal sealed class SessionExceptions {
    data object OngoingSession : SessionExceptions()
    data object GenericError : SessionExceptions()
}
