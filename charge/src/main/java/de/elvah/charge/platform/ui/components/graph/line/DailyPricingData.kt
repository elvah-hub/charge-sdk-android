package de.elvah.charge.platform.ui.components.graph.line

import java.time.LocalDate
import java.time.LocalTime

data class DailyPricingData(
    val date: LocalDate,
    val regularPrice: Double,
    val offers: List<PriceOffer> = emptyList(),
    val currency: String = "€",
    val isSelected: Boolean = false, // Future selection state for entire day
    val slots: List<PriceSlot> = generateSlots(regularPrice, offers)
) {
    companion object {
        private fun generateSlots(regularPrice: Double, offers: List<PriceOffer>): List<PriceSlot> {
            val slots = mutableListOf<PriceSlot>()
            val sortedOffers = offers.sortedBy { it.timeRange.startTime }
            
            var currentTime = LocalTime.of(0, 0)
            val endOfDay = LocalTime.of(23, 59, 59)
            
            for (offer in sortedOffers) {
                // Add regular price slot before offer if there's a gap
                if (currentTime.isBefore(offer.timeRange.startTime)) {
                    slots.add(
                        PriceSlot.RegularPriceSlot(
                            startTime = currentTime,
                            endTime = offer.timeRange.startTime,
                            price = regularPrice,
                            isSelected = false
                        )
                    )
                }
                
                // Add offer slot
                slots.add(
                    PriceSlot.OfferPriceSlot(
                        startTime = offer.timeRange.startTime,
                        endTime = offer.timeRange.endTime,
                        price = offer.discountedPrice,
                        originalPrice = regularPrice,
                        isSelected = offer.isSelected
                    )
                )
                
                currentTime = offer.timeRange.endTime
            }
            
            // Add remaining regular price slot until end of day
            if (currentTime.isBefore(endOfDay)) {
                slots.add(
                    PriceSlot.RegularPriceSlot(
                        startTime = currentTime,
                        endTime = endOfDay,
                        price = regularPrice,
                        isSelected = false
                    )
                )
            }
            
            return slots
        }
    }
    
    // Helper function to create a new instance with updated slots
    fun withUpdatedSlots(updatedSlots: List<PriceSlot>): DailyPricingData {
        return copy(slots = updatedSlots)
    }
}
