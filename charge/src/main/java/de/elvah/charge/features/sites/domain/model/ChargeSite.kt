package de.elvah.charge.features.sites.domain.model

public data class ChargeSite(
    val address: Address,
    val evses: List<ChargePoint>,
    val location: List<Double>,
    val id: String,
    val operatorName: String,
    val prevalentPowerType: String
) {

    public data class Address(
        val streetAddress: List<String?>,
        val postalCode: String,
        val locality: String
    )

    public data class ChargePoint(
        val evseId: String,
        val offer: Offer,
        val powerSpecification: PowerSpecification?,
        val availability: ChargePointAvailability,
        val normalizedEvseId: String
    ) {
        public data class Offer(
            val price: Price,
            val type: String,
            val expiresAt: String,
            val originalPrice: Price? = null,
            val campaignEndsAt: String? = null,
            val signedOffer: String? = null,
            ) {
            public data class Price(
                val energyPricePerKWh: Double,
                val baseFee: Int?,
                val currency: String,
                val blockingFee: BlockingFee?
            ) {
                public data class BlockingFee(
                    val pricePerMinute: Int,
                    val startsAfterMinutes: Int
                )
            }
        }
    }

    public data class PowerSpecification(
        val maxPowerInKW: Float?,
        val type: String
    )
}

public enum class ChargePointAvailability {
    UNAVAILABLE,
    AVAILABLE,
    OUT_OF_SERVICE,
    UNKNOWN,
}
