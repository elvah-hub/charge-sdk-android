package de.elvah.charge.features.sites.ui.components

import kotlin.time.Duration

public data class ChargeBannerActiveSessionRender(
    val id: String,
    val chargeTime: Duration,
    val isSummaryReady: Boolean,
)
