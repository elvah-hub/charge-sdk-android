package de.elvah.charge.features.adhoc_charging.data.repository

import app.cash.turbine.test
import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.adhoc_charging.data.remote.api.ChargingApi
import de.elvah.charge.features.adhoc_charging.data.remote.model.response.ActiveChargeSessionsDto
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class DefaultChargingRepositoryTest {

    private lateinit var chargingApi: ChargingApi
    private lateinit var chargingStore: ChargingStore
    private lateinit var repository: DefaultChargingRepository

    private val testToken = "test_token_123"
    private val testChargingPrefs = mockk<ChargingSessionPrefs> {
        every { token } returns testToken
    }

    @Before
    fun setUp() {
        chargingApi = mockk()
        chargingStore = mockk()
        repository = DefaultChargingRepository(chargingApi, chargingStore)

        every { chargingStore.getChargingPrefs() } returns flowOf(testChargingPrefs)
    }

    @Test
    fun `updateChargingToken calls store setToken`() = runTest {
        val token = "new_token_456"
        coEvery { chargingStore.setToken(token) } returns Unit

        repository.updateChargingToken(token)

        coVerify { chargingStore.setToken(token) }
    }

    @Test
    fun `updateOrganisationDetails calls store saveOrganisationDetails`() = runTest {
        val organisationDetails = createTestOrganisationDetails()
        coEvery { chargingStore.saveOrganisationDetails(organisationDetails) } returns Unit

        repository.updateOrganisationDetails(organisationDetails)

        coVerify { chargingStore.saveOrganisationDetails(organisationDetails) }
    }

    @Test
    fun `fetchChargingSession returns success and emits to flow`() = runTest {
        val dto = createTestActiveChargeSessionsDto()
        coEvery { chargingApi.getActiveChargeSessions("Bearer $testToken") } returns dto

        val result = repository.fetchChargingSession()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { session ->
                assertEquals("DE*KDL*E0000040", session.evseId)
                assertEquals(SessionStatus.CHARGING, session.status)
                assertEquals(15.5, session.consumption, 0.001)
                assertEquals(120, session.duration)
            }
        )

        coVerify { chargingApi.getActiveChargeSessions("Bearer $testToken") }
    }

    @Test
    fun `fetchChargingSession returns failure when API throws exception`() = runTest {
        val exception = RuntimeException("API Error")
        coEvery { chargingApi.getActiveChargeSessions("Bearer $testToken") } throws exception

        val result = repository.fetchChargingSession()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(exception, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `fetchChargingSession emits null to flow when API fails`() = runTest {
        coEvery { chargingApi.getActiveChargeSessions(any()) } throws RuntimeException("API Error")

        repository.fetchChargingSession()

        repository.activeSessions.test {
            assertNull("Expected null to be emitted when API fails", awaitItem())
        }
    }

    @Test
    fun `startChargingSession returns success when API succeeds`() = runTest {
        coEvery { chargingApi.startChargeSessions("Bearer $testToken") } returns mockk()

        val result = repository.startChargingSession()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { success ->
                assertTrue("Expected true", success)
            }
        )

        coVerify { chargingApi.startChargeSessions("Bearer $testToken") }
    }

    @Test
    fun `startChargingSession returns OngoingSession when IllegalStateException thrown`() =
        runTest {
            coEvery { chargingApi.startChargeSessions("Bearer $testToken") } throws IllegalStateException(
                "Session already active"
            )

            val result = repository.startChargingSession()

            assertTrue("Expected Left result", result.isLeft())
            result.fold(
                ifLeft = { error ->
                    assertEquals(SessionExceptions.OngoingSession, error)
                },
                ifRight = { fail("Expected failure but got success: $it") }
            )
        }

    @Test
    fun `startChargingSession returns GenericError for other exceptions`() = runTest {
        coEvery { chargingApi.startChargeSessions("Bearer $testToken") } throws RuntimeException("Generic error")

        val result = repository.startChargingSession()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(SessionExceptions.GenericError, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `stopChargingSession returns success when API succeeds`() = runTest {
        coEvery { chargingApi.stopChargeSession("Bearer $testToken") } returns mockk()

        val result = repository.stopChargingSession()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { success ->
                assertTrue("Expected true", success)
            }
        )

        coVerify { chargingApi.stopChargeSession("Bearer $testToken") }
    }

    @Test
    fun `stopChargingSession returns GenericError when API throws exception`() = runTest {
        coEvery { chargingApi.stopChargeSession("Bearer $testToken") } throws RuntimeException("Stop failed")

        val result = repository.stopChargingSession()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(SessionExceptions.GenericError, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `stopChargingSession returns GenericError even for IllegalStateException`() = runTest {
        coEvery { chargingApi.stopChargeSession("Bearer $testToken") } throws IllegalStateException(
            "Illegal state"
        )

        val result = repository.stopChargingSession()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(
                    "Stop should always return GenericError",
                    SessionExceptions.GenericError,
                    error
                )
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `bearer token is formatted correctly`() = runTest {
        coEvery { chargingApi.getActiveChargeSessions(any()) } returns createTestActiveChargeSessionsDto()

        repository.fetchChargingSession()

        coVerify { chargingApi.getActiveChargeSessions("Bearer $testToken") }
    }

    private fun createTestActiveChargeSessionsDto() = ActiveChargeSessionsDto(
        data = ActiveChargeSessionsDto.Data(
            evseId = "DE*KDL*E0000040",
            status = "CHARGING",
            consumption = 15.5,
            duration = 120
        )
    )

    private fun createTestOrganisationDetails() = OrganisationDetails(
        companyName = "Test CPO",
        logoUrl = "https://example.com/logo.png",
        privacyUrl = "https://example.com/privacy",
        termsOfConditionUrl = "https://example.com/terms",
        supportContacts = SupportContacts(
            email = "test@example.com",
            whatsapp = "+123456789",
            phone = "+987654321",
            agent = "Support Agent"
        )
    )
}
