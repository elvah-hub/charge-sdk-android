package de.elvah.charge.features.adhoc_charging.domain.service.charge.extension

import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal val SessionStatus.isSessionRunning: Boolean
    get() = listOf(
        SessionStatus.STARTED,
        SessionStatus.CHARGING,
        SessionStatus.STOP_REJECTED,
        SessionStatus.STOP_REQUESTED,
    ).contains(this)
