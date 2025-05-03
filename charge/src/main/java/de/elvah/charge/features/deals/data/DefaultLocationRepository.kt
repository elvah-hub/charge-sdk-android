package de.elvah.charge.features.deals.data

import de.elvah.charge.features.deals.domain.model.DealLocation
import de.elvah.charge.features.deals.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow



internal class DefaultLocationRepository(
) : LocationRepository {

    private val _location: MutableSharedFlow<DealLocation> =
        MutableSharedFlow<DealLocation>(replay = 1)
    override val location: Flow<DealLocation> = _location.asSharedFlow()

    override suspend fun updateLocation(location: DealLocation) {
        _location.emit(location)
    }

    /*
    init {
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        runBlocking {
            delay(1000)
            Log.d("LocationRepository", "startLocationUpdates")
            updateLocation(DealLocation(minLat = 5.0, minLng = 4.0, maxLat = 52.0, maxLng = 50.0))
        }
    }

     */
}