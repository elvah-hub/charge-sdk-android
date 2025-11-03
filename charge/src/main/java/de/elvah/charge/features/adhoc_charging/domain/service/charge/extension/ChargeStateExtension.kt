package de.elvah.charge.features.adhoc_charging.domain.service.charge.extension

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeState

internal val ChargeState.isSummaryState: Boolean
    get() = this == ChargeState.SUMMARY
