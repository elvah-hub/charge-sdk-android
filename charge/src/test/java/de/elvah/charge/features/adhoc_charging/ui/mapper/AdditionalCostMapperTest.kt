package de.elvah.charge.features.adhoc_charging.ui.mapper

import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import de.elvah.charge.features.sites.domain.model.BlockingFee
import de.elvah.charge.features.sites.domain.model.BlockingFeeTimeSlot
import de.elvah.charge.features.sites.domain.model.Pricing
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class AdditionalCostMapperTest {

    @Test
    fun `toUI maps all fields correctly when all values are present`() {
        val baseFee = Pricing(5.0, "EUR")
        val blockingFeePricing = Pricing(0.15, "EUR")
        val maxAmount = Pricing(10.0, "EUR")
        val timeSlots = listOf(
            BlockingFeeTimeSlot("08:00:00", "18:00:00"),
            BlockingFeeTimeSlot("19:00:00", "23:00:00")
        )
        val blockingFee = BlockingFee(
            pricePerMinute = blockingFeePricing,
            startsAfterMinutes = 30,
            maxAmount = maxAmount,
            timeSlots = timeSlots,
            currency = "EUR"
        )
        val additionalCosts = AdditionalCosts(
            baseFee = baseFee,
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(baseFee, result?.activationFee)
        assertEquals(blockingFeePricing, result?.blockingFee)
        assertEquals(maxAmount, result?.blockingFeeMaxPrice)
        assertEquals(30, result?.startsAfterMinutes)
        assertEquals(2, result?.timeSlots?.size)
        assertEquals("08:00", result?.timeSlots?.get(0)?.startTime)
        assertEquals("18:00", result?.timeSlots?.get(0)?.endTime)
        assertEquals("19:00", result?.timeSlots?.get(1)?.startTime)
        assertEquals("23:00", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI returns null when both baseFee and blockingFee are null`() {
        val additionalCosts = AdditionalCosts(
            baseFee = null,
            blockingFee = null,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertNull(result)
    }

    @Test
    fun `toUI returns object when only baseFee is present`() {
        val baseFee = Pricing(3.5, "USD")
        val additionalCosts = AdditionalCosts(
            baseFee = baseFee,
            blockingFee = null,
            currency = "USD"
        )

        val result = additionalCosts.toUI()

        assertEquals(baseFee, result?.activationFee)
        assertNull(result?.blockingFee)
        assertNull(result?.blockingFeeMaxPrice)
        assertNull(result?.startsAfterMinutes)
        assertNull(result?.timeSlots)
    }

    @Test
    fun `toUI returns object when only blockingFee is present`() {
        val blockingFeePricing = Pricing(0.25, "GBP")
        val blockingFee = BlockingFee(
            pricePerMinute = blockingFeePricing,
            startsAfterMinutes = 60,
            maxAmount = null,
            timeSlots = null,
            currency = "GBP"
        )
        val additionalCosts = AdditionalCosts(
            baseFee = null,
            blockingFee = blockingFee,
            currency = "GBP"
        )

        val result = additionalCosts.toUI()

        assertNull(result?.activationFee)
        assertEquals(blockingFeePricing, result?.blockingFee)
        assertNull(result?.blockingFeeMaxPrice)
        assertEquals(60, result?.startsAfterMinutes)
        assertNull(result?.timeSlots)
    }

    @Test
    fun `toUI formats hours below 10 with leading zero`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("01:30:00", "09:45:00"),
            BlockingFeeTimeSlot("05:15:00", "07:00:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals("01:30", result?.timeSlots?.get(0)?.startTime)
        assertEquals("09:45", result?.timeSlots?.get(0)?.endTime)
        assertEquals("05:15", result?.timeSlots?.get(1)?.startTime)
        assertEquals("07:00", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI formats minutes below 10 with leading zero`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("14:05:00", "16:09:00"),
            BlockingFeeTimeSlot("20:01:00", "22:03:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals("14:05", result?.timeSlots?.get(0)?.startTime)
        assertEquals("16:09", result?.timeSlots?.get(0)?.endTime)
        assertEquals("20:01", result?.timeSlots?.get(1)?.startTime)
        assertEquals("22:03", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI formats both hours and minutes below 10 with leading zeros`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("01:05:00", "09:09:00"),
            BlockingFeeTimeSlot("05:01:00", "07:03:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals("01:05", result?.timeSlots?.get(0)?.startTime)
        assertEquals("09:09", result?.timeSlots?.get(0)?.endTime)
        assertEquals("05:01", result?.timeSlots?.get(1)?.startTime)
        assertEquals("07:03", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI handles midnight and edge time values`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("00:00:00", "23:59:00"),
            BlockingFeeTimeSlot("12:30:00", "00:00:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals("00:00", result?.timeSlots?.get(0)?.startTime)
        assertEquals("23:59", result?.timeSlots?.get(0)?.endTime)
        assertEquals("12:30", result?.timeSlots?.get(1)?.startTime)
        assertEquals("00:00", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI filters out time slots with empty startTime or endTime`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("", "18:00:00"),
            BlockingFeeTimeSlot("08:00:00", ""),
            BlockingFeeTimeSlot("", ""),
            BlockingFeeTimeSlot("09:00:00", "17:00:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(1, result?.timeSlots?.size)
        assertEquals("09:00", result?.timeSlots?.get(0)?.startTime)
        assertEquals("17:00", result?.timeSlots?.get(0)?.endTime)
    }

    @Test
    fun `toUI returns only properly formatted time slot times`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("invalid:time", "18:00:00"),
            BlockingFeeTimeSlot("08:00:00", "invalid:time"),
            BlockingFeeTimeSlot("not-time-format", "also-invalid"),
            BlockingFeeTimeSlot("09:00:00", "17:00:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(1, result?.timeSlots?.size)
        assertEquals("09:00", result?.timeSlots?.get(0)?.startTime)
        assertEquals("17:00", result?.timeSlots?.get(0)?.endTime)
    }

    @Test
    fun `toUI handles time strings without seconds`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("08:30", "18:45"),
            BlockingFeeTimeSlot("19:15", "23:30")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(2, result?.timeSlots?.size)
        assertEquals("08:30", result?.timeSlots?.get(0)?.startTime)
        assertEquals("18:45", result?.timeSlots?.get(0)?.endTime)
        assertEquals("19:15", result?.timeSlots?.get(1)?.startTime)
        assertEquals("23:30", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI handles time strings with extra components`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("08:30:00:123", "18:45:00:456"),
            BlockingFeeTimeSlot("19:15:00:789", "23:30:00:000")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(2, result?.timeSlots?.size)
        assertEquals("08:30", result?.timeSlots?.get(0)?.startTime)
        assertEquals("18:45", result?.timeSlots?.get(0)?.endTime)
        assertEquals("19:15", result?.timeSlots?.get(1)?.startTime)
        assertEquals("23:30", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI returns empty time slots list when all time slots are filtered out`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("", "18:00:00"),
            BlockingFeeTimeSlot("08:00:00", ""),
            BlockingFeeTimeSlot("", ""),
            BlockingFeeTimeSlot("invalid", "also-invalid")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(0, result?.timeSlots?.size)
    }

    @Test
    fun `toUI handles empty time slots list`() {
        val blockingFee = createBlockingFeeWithTimeSlots(emptyList())
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(0, result?.timeSlots?.size)
    }

    @Test
    fun `toUI handles double digit hours and minutes correctly`() {
        val timeSlots = listOf(
            BlockingFeeTimeSlot("10:30:00", "18:45:00"),
            BlockingFeeTimeSlot("19:15:00", "23:30:00")
        )
        val blockingFee = createBlockingFeeWithTimeSlots(timeSlots)
        val additionalCosts = AdditionalCosts(
            baseFee = Pricing(1.0, "EUR"),
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals("10:30", result?.timeSlots?.get(0)?.startTime)
        assertEquals("18:45", result?.timeSlots?.get(0)?.endTime)
        assertEquals("19:15", result?.timeSlots?.get(1)?.startTime)
        assertEquals("23:30", result?.timeSlots?.get(1)?.endTime)
    }

    @Test
    fun `toUI handles zero values for pricing and time correctly`() {
        val baseFee = Pricing(0.0, "EUR")
        val blockingFeePricing = Pricing(0.0, "EUR")
        val maxAmount = Pricing(0.0, "EUR")
        val timeSlots = listOf(BlockingFeeTimeSlot("00:00:00", "00:00:00"))
        val blockingFee = BlockingFee(
            pricePerMinute = blockingFeePricing,
            startsAfterMinutes = 0,
            maxAmount = maxAmount,
            timeSlots = timeSlots,
            currency = "EUR"
        )
        val additionalCosts = AdditionalCosts(
            baseFee = baseFee,
            blockingFee = blockingFee,
            currency = "EUR"
        )

        val result = additionalCosts.toUI()

        assertEquals(baseFee, result?.activationFee)
        assertEquals(blockingFeePricing, result?.blockingFee)
        assertEquals(maxAmount, result?.blockingFeeMaxPrice)
        assertEquals(0, result?.startsAfterMinutes)
        assertEquals(1, result?.timeSlots?.size)
        assertEquals("00:00", result?.timeSlots?.get(0)?.startTime)
        assertEquals("00:00", result?.timeSlots?.get(0)?.endTime)
    }

    private fun createBlockingFeeWithTimeSlots(timeSlots: List<BlockingFeeTimeSlot>) = BlockingFee(
        pricePerMinute = Pricing(0.15, "EUR"),
        startsAfterMinutes = 30,
        maxAmount = Pricing(10.0, "EUR"),
        timeSlots = timeSlots,
        currency = "EUR"
    )
}
