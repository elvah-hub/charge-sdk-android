package de.elvah.charge.features.sites.domain.usecase

import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.sites.domain.exceptions.EmptyResultsException
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Offer
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.domain.model.Pricing
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalTime::class)
class GetBestSiteTest {

    private lateinit var getSites: GetSites

    @Before
    fun setUp() {
        getSites = mockk()
    }

    @Test
    fun `returns EmptyResultsException when offer has empty campaignEndsAt`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        val site = buildSite(campaignEndsAt = "")
        coEvery { getSites(any()) } returns listOf(site).right()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { assertTrue(it is EmptyResultsException) },
            ifRight = { error("Expected EmptyResultsException but got site") }
        )
    }

    @Test
    fun `returns EmptyResultsException when offer has null campaignEndsAt`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        val site = buildSite(campaignEndsAt = null)
        coEvery { getSites(any()) } returns listOf(site).right()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { assertTrue(it is EmptyResultsException) },
            ifRight = { error("Expected EmptyResultsException but got site") }
        )
    }

    @Test
    fun `returns EmptyResultsException when offer has malformed campaignEndsAt`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        val site = buildSite(campaignEndsAt = "not-a-valid-date")
        coEvery { getSites(any()) } returns listOf(site).right()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { assertTrue(it is EmptyResultsException) },
            ifRight = { error("Expected EmptyResultsException but got site") }
        )
    }

    @Test
    fun `returns site when offer has valid future campaignEndsAt`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        val futureDate = Clock.System.now().plus(24.hours).toString()
        val site = buildSite(campaignEndsAt = futureDate)
        coEvery { getSites(any()) } returns listOf(site).right()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isRight())
        result.fold(
            ifLeft = { error("Expected site but got: $it") },
            ifRight = { assertTrue(it.id == "site_1") }
        )
    }

    @Test
    fun `returns EmptyResultsException when offer has past campaignEndsAt`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        val pastDate = Clock.System.now().minus(24.hours).toString()
        val site = buildSite(campaignEndsAt = pastDate)
        coEvery { getSites(any()) } returns listOf(site).right()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { assertTrue(it is EmptyResultsException) },
            ifRight = { error("Expected EmptyResultsException but got site") }
        )
    }

    @Test
    fun `propagates failure from GetSites`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        val exception = RuntimeException("network error")
        coEvery { getSites(any()) } returns exception.left()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { assertTrue(it is RuntimeException) },
            ifRight = { error("Expected failure but got site") }
        )
    }

    @Test
    fun `returns EmptyResultsException when site list is empty`() = runTest {
        val useCase = GetBestSite(getSites, StandardTestDispatcher(testScheduler))
        coEvery { getSites(any()) } returns emptyList<ChargeSite>().right()

        val result = useCase(GetBestSite.Params())

        assertTrue(result.isLeft())
        result.fold(
            ifLeft = { assertTrue(it is EmptyResultsException) },
            ifRight = { error("Expected EmptyResultsException but got site") }
        )
    }

    private fun buildSite(campaignEndsAt: String?) = ChargeSite(
        id = "site_1",
        operatorName = "Test CPO",
        address = ChargeSite.Address(
            streetAddress = listOf("Test Street 1"),
            postalCode = "12345",
            locality = "Test City",
        ),
        location = listOf(52.5, 13.4),
        prevalentPowerType = "AC",
        dynamicPricingAvailable = false,
        evses = listOf(
            ChargeSite.ChargePoint(
                evseId = "DE*TEST*E001",
                normalizedEvseId = "DE*TEST*E001",
                availability = ChargePointAvailability.AVAILABLE,
                powerSpecification = null,
                offer = Offer(
                    price = Price(
                        energyPricePerKWh = Pricing(value = 0.25, currency = "EUR"),
                        baseFee = null,
                        blockingFee = null,
                        currency = "EUR",
                    ),
                    type = "CAMPAIGN",
                    expiresAt = "2099-01-01T00:00:00Z",
                    originalPrice = null,
                    campaignEndsAt = campaignEndsAt,
                    signedOffer = null,
                )
            )
        )
    )
}
