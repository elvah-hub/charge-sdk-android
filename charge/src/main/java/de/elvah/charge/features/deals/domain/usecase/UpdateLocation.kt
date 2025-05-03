package de.elvah.charge.features.deals.domain.usecase

import de.elvah.charge.features.deals.domain.model.DealLocation
import de.elvah.charge.features.deals.domain.repository.LocationRepository


internal class UpdateLocation(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double) {
        locationRepository.updateLocation(DealLocation(minLat, minLng, maxLat, maxLng))
    }
}