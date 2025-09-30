package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import arrow.core.right
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import de.elvah.charge.public_api.banner.EvseId

internal class FakeSitesRepository(simulatorFlow: SimulatorFlow) : SitesRepository {

    private var chargeSites: List<ChargeSite> = emptyList()

    override fun getChargeSite(siteId: String): Either<Throwable, ChargeSite> {
        return chargeSites.first { it.id == siteId }.right()
    }

    override fun updateChargeSite(site: ChargeSite) {
        if (chargeSites.none { it.id == site.id }) {
            chargeSites = chargeSites + site
        } else {
            chargeSites = chargeSites.map {
                if (it.id == site.id) {
                    site
                } else {
                    it
                }
            }
        }
    }

    override suspend fun getChargeSites(
        boundingBox: BoundingBox?,
        campaignId: String?,
        organisationId: String?,
        offerType: OfferType?,
        evseIds: List<EvseId>?
    ): Either<Throwable, List<ChargeSite>> {
        return MockData.chargeSites.right().also {
            it.getOrNull()?.let {
                chargeSites = it
            }
        }
    }

    override suspend fun getSignedOffer(
        siteId: String,
        evseId: String
    ): Either<Exception, ChargeSite> {
        return MockData.chargeSites.first().right()
    }

    override suspend fun getSiteScheduledPricing(siteId: String): Either<Throwable, ScheduledPricing> {
        return createMockScheduledPricing().right()
    }

    private fun createMockScheduledPricing(): ScheduledPricing {
        val blockingFee = ScheduledPricing.Price.BlockingFee(
            pricePerMinute = 5,
            startsAfterMinutes = 240
        )

        val standardPrice = ScheduledPricing.Price(
            energyPricePerKWh = 0.35,
            baseFee = 0,
            currency = "EUR",
            blockingFee = blockingFee
        )

        val timeSlots = listOf(
            ScheduledPricing.TimeSlot(
                isDiscounted = false,
                price = standardPrice,
                from = "00:00",
                to = "06:00"
            ),
            ScheduledPricing.TimeSlot(
                isDiscounted = true,
                price = standardPrice.copy(energyPricePerKWh = 0.25),
                from = "06:00",
                to = "10:00"
            ),
            ScheduledPricing.TimeSlot(
                isDiscounted = false,
                price = standardPrice,
                from = "10:00",
                to = "14:00"
            ),
            ScheduledPricing.TimeSlot(
                isDiscounted = true,
                price = standardPrice.copy(energyPricePerKWh = 0.28),
                from = "14:00",
                to = "18:00"
            ),
            ScheduledPricing.TimeSlot(
                isDiscounted = false,
                price = standardPrice,
                from = "18:00",
                to = "24:00"
            )
        )

        val day = ScheduledPricing.Day(
            lowestPrice = standardPrice.copy(energyPricePerKWh = 0.25),
            trend = "down",
            timeSlots = timeSlots
        )

        val dailyPricing = ScheduledPricing.DailyPricing(
            yesterday = day.copy(trend = "up"),
            today = day,
            tomorrow = day.copy(trend = "stable")
        )

        return ScheduledPricing(
            dailyPricing = dailyPricing,
            standardPrice = standardPrice
        )
    }

    override suspend fun updateChargePointAvailabilities(siteId: String): Either<Throwable, List<ChargeSite.ChargePoint>> {
        TODO("Not yet implemented")
    }

    companion object {
        const val MIN_LAT_KEY = "minLat"
        const val MAX_LAT_KEY = "maxLat"
        const val MIN_LNG_KEY = "minLng"
        const val MAX_LNG_KEY = "maxLng"
        const val CAMPAIGN_ID_KEY = "campaignId"
        const val ORGANISATION_ID_KEY = "organisationId"
        const val OFFER_TYPE_KEY = "offerType"
    }
}
