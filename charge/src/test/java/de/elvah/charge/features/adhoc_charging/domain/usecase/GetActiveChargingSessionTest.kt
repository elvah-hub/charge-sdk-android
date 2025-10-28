package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import io.mockk.mockk
import org.junit.Before

class GetActiveChargingSessionTest {

    private lateinit var chargeService: ChargeService

    @Before
    fun setUp() {
        chargeService = mockk()
    }

    private fun getUseCase(
        config: () -> Unit,
    ): GetChargingSession {
        config()

        return GetChargingSession(chargeService)
    }

    /*@Test
    fun `charge session states are received successfully`() = runTest {
        val startRejected = createTestChargingSession(status = SessionStatus.START_REJECTED)
        val startRequested = createTestChargingSession(status = SessionStatus.START_REQUESTED)
        val started = createTestChargingSession(status = SessionStatus.STARTED)
        val charging = createTestChargingSession(status = SessionStatus.CHARGING)
        val stopRequested = createTestChargingSession(status = SessionStatus.STOP_REQUESTED)
        val stopped = createTestChargingSession(status = SessionStatus.STOPPED)
        val stopRejected = createTestChargingSession(status = SessionStatus.STOP_REJECTED)

        val useCase = getUseCase {
            coEvery { chargeService.chargeSession } returns flowOf(
                startRequested,
                startRejected,
                startRequested,
                started,
                charging,
                stopRequested,
                stopped,
                startRejected,
                stopRejected,
            )
        }

        useCase.activeChargingSession.test {
            assertEquals(startRequested, awaitItem())
            assertEquals(startRejected, awaitItem())
            assertEquals(startRequested, awaitItem())
            assertEquals(started, awaitItem())
            assertEquals(charging, awaitItem())
            assertEquals(stopRequested, awaitItem())
            assertEquals(stopped, awaitItem())
            assertEquals(startRejected, awaitItem())
            assertEquals(stopRejected, awaitItem())
            awaitComplete()
        }
    }*/

    private fun createTestChargingSession(
        status: SessionStatus = SessionStatus.CHARGING,
    ) = ChargingSession(
        evseId = "DE*KDL*E0000040",
        consumption = 15.5,
        duration = 120,
        status = status,
    )
}
