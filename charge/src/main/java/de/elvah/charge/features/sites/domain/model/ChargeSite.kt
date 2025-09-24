package de.elvah.charge.features.sites.domain.model

data class ChargeSite(
    val address: Address,
    val evses: List<ChargePoint>,
    val location: List<Double>,
    val id: String,
    val operatorName: String,
    val prevalentPowerType: String
) {

    data class Address(
        val streetAddress: List<String?>,
        val postalCode: String,
        val locality: String
    )

    data class ChargePoint(
        val evseId: String,
        val offer: Offer,
        val powerSpecification: PowerSpecification?,
        val availability: ChargePointAvailability,
        val normalizedEvseId: String
    ) {
        data class Offer(
            val price: Price,
            val type: String,
            val expiresAt: String,
            val originalPrice: Price? = null,
            val campaignEndsAt: String? = null,
            val signedOffer: String? = null,

            ) {
            data class Price(
                val energyPricePerKWh: Double,
                val baseFee: Int?,
                val currency: String,
                val blockingFee: BlockingFee?
            ) {
                data class BlockingFee(
                    val pricePerMinute: Int,
                    val startsAfterMinutes: Int
                )
            }
        }
    }

    data class PowerSpecification(
        val maxPowerInKW: Float?,
        val type: String
    )
}

enum class ChargePointAvailability {
    UNAVAILABLE,
    AVAILABLE,
    OUT_OF_SERVICE,
    UNKNOWN,
}
