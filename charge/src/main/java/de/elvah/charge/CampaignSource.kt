package de.elvah.charge

import de.elvah.charge.features.deals.domain.usecase.UpdateLocation
import org.koin.java.KoinJavaComponent

class CampaignSource() {

    private val updateLocation: UpdateLocation by KoinJavaComponent.inject(UpdateLocation::class.java)

    suspend fun dealsAt(coordinates: Coordinates) {
        dealsAt(
            coordinates.minLat,
            coordinates.minLng,
            coordinates.maxLat,
            coordinates.maxLng
        )
    }

    suspend fun dealsAt(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double) {
        updateLocation(minLat, minLng, maxLat, maxLng)
    }

    class Coordinates(
        val minLat: Double,
        val minLng: Double,
        val maxLat: Double,
        val maxLng: Double
    )
}