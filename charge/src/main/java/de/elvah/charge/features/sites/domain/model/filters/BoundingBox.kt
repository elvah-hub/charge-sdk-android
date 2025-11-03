package de.elvah.charge.features.sites.domain.model.filters

public data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLng: Double,
    val maxLng: Double,
)
