package de.elvah.charge.features.deals.domain.usecase

import de.elvah.charge.features.deals.domain.model.DealLocation
import de.elvah.charge.features.deals.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow


internal class GetLocation(
    private val locationRepository: LocationRepository,
) {
    operator fun invoke(): Flow<DealLocation> {
        return locationRepository.location
    }
}