package de.elvah.charge.features.payments.data.repository

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.data.remote.api.ChargeSettlementApi
import de.elvah.charge.features.payments.data.remote.api.IntegrateApi
import de.elvah.charge.features.payments.domain.model.PublishableKey
import de.elvah.charge.features.payments.data.remote.model.OrganisationDetailsDto
import de.elvah.charge.features.payments.data.remote.model.response.AuthorisationAmount
import de.elvah.charge.features.payments.data.remote.model.response.CreatePaymentIntentResponse
import de.elvah.charge.features.payments.data.remote.model.response.Data
import de.elvah.charge.features.payments.data.remote.model.response.GetPublishableKeyResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class DefaultPaymentsRepositoryTest {

    private lateinit var chargeSettlementApi: ChargeSettlementApi
    private lateinit var integrateApi: IntegrateApi
    private lateinit var chargingStore: ChargingStore
    private lateinit var repository: DefaultPaymentsRepository

    @Before
    fun setUp() {
        chargeSettlementApi = mockk()
        integrateApi = mockk()
        chargingStore = mockk()
        repository = DefaultPaymentsRepository(chargeSettlementApi, integrateApi, chargingStore)

        val testSessionDetails = mockk<ChargingSessionPrefs> {
            every { evseId } returns "DE*KDL*E0000040"
            every { cpoName } returns "Test CPO"
        }
        every { chargingStore.getChargingPrefs() } returns flowOf(testSessionDetails)
    }

    @Test
    fun `createPaymentIntent returns success`() = runTest {
        val signedOffer = "signed_offer_123"
        val response = CreatePaymentIntentResponse(
            data = Data(
                paymentIntentId = "pi_test_123",
                accountId = "acct_test_456",
                paymentId = "pay_test_789",
                clientSecret = "pi_secret_xyz",
                authorisationAmount = AuthorisationAmount(
                    value = 25.50,
                    currency = "EUR"
                ),
                organisationDetails = OrganisationDetailsDto(
                    companyName = "Test CPO",
                    logoUrl = "https://example.com/logo.png",
                    privacyUrl = "https://example.com/privacy",
                    termsOfConditionUrl = "https://example.com/terms",
                    supportContacts = emptyList()
                )
            )
        )
        coEvery { chargeSettlementApi.createPaymentIntent(any()) } returns response
        coEvery { chargingStore.saveOrganisationDetails(any()) } returns Unit

        val result = repository.createPaymentIntent(signedOffer)

        assertTrue("Expected Right result", result.isRight())
        coVerify { chargeSettlementApi.createPaymentIntent(any()) }
        coVerify { chargingStore.saveOrganisationDetails(any()) }
    }

    @Test
    fun `createPaymentIntent returns failure when API throws exception`() = runTest {
        val exception = RuntimeException("Payment intent creation failed")
        coEvery { chargeSettlementApi.createPaymentIntent(any()) } throws exception

        val result = repository.createPaymentIntent("signed_offer")

        assertTrue("Expected Left result", result.isLeft())
    }

    @Test
    fun `getPublishableKey returns success`() = runTest {
        val publishableKey = "pk_test_123456789"
        val response = GetPublishableKeyResponse(
            data = GetPublishableKeyResponse.Data(publishableKey = publishableKey)
        )
        coEvery { integrateApi.getPublishableKey() } returns response

        val result = repository.getPublishableKey()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { key ->
                assertEquals(PublishableKey(publishableKey), key)
            }
        )

        coVerify { integrateApi.getPublishableKey() }
    }
}
