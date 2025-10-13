package de.elvah.charge.features.payments.domain.usecase

import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.model.PaymentIntent
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

internal class GetPaymentConfigurationTest {

    private lateinit var paymentsRepository: PaymentsRepository
    private lateinit var sitesRepository: SitesRepository
    private lateinit var chargingStore: ChargingStore
    private lateinit var useCase: GetPaymentConfiguration

    @Before
    fun setUp() {
        paymentsRepository = mockk()
        sitesRepository = mockk()
        chargingStore = mockk()
        useCase = GetPaymentConfiguration(paymentsRepository, sitesRepository, chargingStore)
        
        // Mock ChargingStore calls that are always called on success path
        coEvery { chargingStore.setPaymentId(any()) } just Runs
        coEvery { chargingStore.setEvseId(any()) } just Runs
    }

    @Test
    fun `invoke returns success when all operations succeed`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        val signedOffer = "signed_offer_token"
        val publishableKey = "pk_test_123"
        
        val chargeSite = createChargeSiteWithSignedOffer(signedOffer)
        val paymentIntent = createPaymentIntent()
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSite.right()
        coEvery { paymentsRepository.createPaymentIntent(signedOffer) } returns paymentIntent.right()
        coEvery { paymentsRepository.getPublishableKey() } returns publishableKey.right()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { config ->
                assertEquals(publishableKey, config.publishableKey)
                assertEquals(paymentIntent.accountId, config.accountId)
                assertEquals(paymentIntent.clientSecret, config.clientSecret)
                assertEquals(paymentIntent.paymentId, config.paymentId)
            }
        )
        
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.createPaymentIntent(signedOffer) }
        coVerify { paymentsRepository.getPublishableKey() }
        coVerify { chargingStore.setPaymentId(paymentIntent.paymentId) }
        coVerify { chargingStore.setEvseId(evseId) }
    }

    @Test
    fun `invoke returns NoOfferFound when getSignedOffer returns Left`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        val exception = RuntimeException("Site not found")
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns exception.left()
        coEvery { paymentsRepository.getPublishableKey() } returns "pk_test_123".right()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue("Expected NoOfferFound error", error is PaymentConfigErrors.NoOfferFound)
                assertEquals(exception.cause, error.throwable)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
        
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.getPublishableKey() }
        coVerify(exactly = 0) { paymentsRepository.createPaymentIntent(any()) }
        coVerify(exactly = 0) { chargingStore.setPaymentId(any()) }
        coVerify(exactly = 0) { chargingStore.setEvseId(any()) }
    }

    @Test
    fun `invoke returns NoOfferFound when charge site has no evses`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        
        val chargeSiteWithNoEvses = createChargeSite(emptyList())
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSiteWithNoEvses.right()
        coEvery { paymentsRepository.getPublishableKey() } returns "pk_test_123".right()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue("Expected NoOfferFound error", error is PaymentConfigErrors.NoOfferFound)
                assertNull("Should have null throwable since Exception has no cause", error.throwable)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
        
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.getPublishableKey() }
        coVerify(exactly = 0) { paymentsRepository.createPaymentIntent(any()) }
        coVerify(exactly = 0) { chargingStore.setPaymentId(any()) }
        coVerify(exactly = 0) { chargingStore.setEvseId(any()) }
    }

    @Test
    fun `invoke returns NoOfferFound when first evse has no signed offer`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        
        val chargeSiteWithNoSignedOffer = createChargeSiteWithSignedOffer(null)
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSiteWithNoSignedOffer.right()
        coEvery { paymentsRepository.getPublishableKey() } returns "pk_test_123".right()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue("Expected NoOfferFound error", error is PaymentConfigErrors.NoOfferFound)
                assertNull("Should have null throwable since Exception has no cause", error.throwable)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
        
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.getPublishableKey() }
        coVerify(exactly = 0) { paymentsRepository.createPaymentIntent(any()) }
        coVerify(exactly = 0) { chargingStore.setPaymentId(any()) }
        coVerify(exactly = 0) { chargingStore.setEvseId(any()) }
    }

    @Test
    fun `invoke returns NoOfferFound when createPaymentIntent returns Left`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        val signedOffer = "signed_offer_token"
        val exception = RuntimeException("Payment intent creation failed")
        
        val chargeSite = createChargeSiteWithSignedOffer(signedOffer)
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSite.right()
        coEvery { paymentsRepository.createPaymentIntent(signedOffer) } returns exception.left()
        coEvery { paymentsRepository.getPublishableKey() } returns "pk_test_123".right()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue("Expected NoOfferFound error", error is PaymentConfigErrors.NoOfferFound)
                assertEquals(exception.cause, error.throwable)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
        
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.createPaymentIntent(signedOffer) }
        coVerify { paymentsRepository.getPublishableKey() }
        coVerify(exactly = 0) { chargingStore.setPaymentId(any()) }
        coVerify(exactly = 0) { chargingStore.setEvseId(any()) }
    }

    @Test
    fun `invoke returns NoPublishableKey when getPublishableKey returns Left`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        val signedOffer = "signed_offer_token"
        val exception = RuntimeException("Publishable key not found")
        
        val chargeSite = createChargeSiteWithSignedOffer(signedOffer)
        val paymentIntent = createPaymentIntent()
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSite.right()
        coEvery { paymentsRepository.createPaymentIntent(signedOffer) } returns paymentIntent.right()
        coEvery { paymentsRepository.getPublishableKey() } returns exception.left()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertTrue("Expected NoPublishableKey error", error is PaymentConfigErrors.NoPublishableKey)
                assertEquals(exception.cause, error.throwable)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
        
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.createPaymentIntent(signedOffer) }
        coVerify { paymentsRepository.getPublishableKey() }
        coVerify { chargingStore.setPaymentId(paymentIntent.paymentId) }
        coVerify { chargingStore.setEvseId(evseId) }
    }

    @Test
    fun `invoke still sets payment id and evse id even when publishable key fails`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        val signedOffer = "signed_offer_token"
        val exception = RuntimeException("Publishable key not found")
        
        val chargeSite = createChargeSiteWithSignedOffer(signedOffer)
        val paymentIntent = createPaymentIntent()
        
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSite.right()
        coEvery { paymentsRepository.createPaymentIntent(signedOffer) } returns paymentIntent.right()
        coEvery { paymentsRepository.getPublishableKey() } returns exception.left()
        
        useCase.invoke(siteId, evseId)
        
        coVerify { chargingStore.setPaymentId(paymentIntent.paymentId) }
        coVerify { chargingStore.setEvseId(evseId) }
    }

    @Test
    fun `invoke handles concurrent async operations correctly`() = runTest {
        val siteId = "site_123"
        val evseId = "evse_456"
        val signedOffer = "signed_offer_token"
        val publishableKey = "pk_test_123"
        
        val chargeSite = createChargeSiteWithSignedOffer(signedOffer)
        val paymentIntent = createPaymentIntent()
        
        // Both async operations should be called concurrently
        coEvery { sitesRepository.getSignedOffer(siteId, evseId) } returns chargeSite.right()
        coEvery { paymentsRepository.createPaymentIntent(signedOffer) } returns paymentIntent.right()
        coEvery { paymentsRepository.getPublishableKey() } returns publishableKey.right()
        
        val result = useCase.invoke(siteId, evseId)
        
        assertTrue("Expected Right result", result.isRight())
        
        // Verify that both repository calls were made
        coVerify { sitesRepository.getSignedOffer(siteId, evseId) }
        coVerify { paymentsRepository.getPublishableKey() }
    }

    private fun createChargeSiteWithSignedOffer(signedOffer: String?): ChargeSite {
        val chargePoints = listOf(
            createChargePoint(signedOffer = signedOffer)
        )
        return createChargeSite(chargePoints)
    }

    private fun createChargeSite(evses: List<ChargeSite.ChargePoint>): ChargeSite {
        return ChargeSite(
            address = ChargeSite.Address(
                streetAddress = listOf("Test Street 123"),
                postalCode = "12345",
                locality = "Test City"
            ),
            evses = evses,
            location = listOf(52.5200, 13.4050),
            id = "site_test_123",
            operatorName = "Test Operator",
            prevalentPowerType = "AC"
        )
    }

    private fun createChargePoint(signedOffer: String?): ChargeSite.ChargePoint {
        return ChargeSite.ChargePoint(
            evseId = "evse_test_456",
            offer = ChargeSite.ChargePoint.Offer(
                price = ChargeSite.ChargePoint.Offer.Price(
                    energyPricePerKWh = Pricing(value = 0.30, currency = "EUR"),
                    baseFee = null,
                    blockingFee = null,
                    currency = "EUR"
                ),
                type = "DEAL",
                expiresAt = "2024-12-31T23:59:59Z",
                signedOffer = signedOffer
            ),
            powerSpecification = null,
            availability = ChargePointAvailability.AVAILABLE,
            normalizedEvseId = "evse_test_456"
        )
    }

    private fun createPaymentIntent(): PaymentIntent {
        return PaymentIntent(
            paymentId = "payment_123",
            paymentIntentId = "pi_test_456",
            accountId = "account_789",
            clientSecret = "secret_123",
            amount = 25.50,
            currency = "EUR"
        )
    }
}