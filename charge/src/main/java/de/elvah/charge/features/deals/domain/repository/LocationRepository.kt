package de.elvah.charge.features.deals.domain.repository

import de.elvah.charge.features.deals.domain.model.DealLocation
import kotlinx.coroutines.flow.Flow

internal interface LocationRepository {

    val location: Flow<DealLocation>

    suspend fun updateLocation(location: DealLocation)
}