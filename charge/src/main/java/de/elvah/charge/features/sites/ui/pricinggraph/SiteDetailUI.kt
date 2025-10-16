package de.elvah.charge.features.sites.ui.pricinggraph

internal data class SiteDetailUI(
    val operatorName: String,
    val address: String?,
    val coordinates: Pair<Double, Double>,
)
