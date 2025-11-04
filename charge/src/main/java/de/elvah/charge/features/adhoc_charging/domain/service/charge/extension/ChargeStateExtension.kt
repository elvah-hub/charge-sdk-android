package de.elvah.charge.features.adhoc_charging.domain.service.charge.extension

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeServiceState

internal val ChargeServiceState.isSummaryReady: Boolean
    get() = this == ChargeServiceState.SUMMARY
