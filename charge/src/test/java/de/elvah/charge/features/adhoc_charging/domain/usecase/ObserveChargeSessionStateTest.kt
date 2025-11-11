package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.common.createTestChargingSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargingSessionState
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class ObserveChargeSessionStateTest {

    private lateinit var chargeService: ChargeService
    private lateinit var stateFlowMock: MutableStateFlow<ChargingSessionState>

    private val initialValue = ChargingSessionState(
        isSessionRunning = false,
        isSessionSummaryReady = false,
        lastSessionData = null,
    )

    @Before
    fun setup() {
        stateFlowMock = MutableStateFlow(initialValue)
        chargeService = mockk(relaxed = true)
        every { chargeService.chargeSessionState } returns stateFlowMock
    }

    @Test
    fun `when invoked then return the same initial value from charge service`() {
        val useCase = getUseCase()
        val result = useCase.invoke()

        assertSame(
            "use case should return same instance from charge service",
            initialValue,
            result.value,
        )

        verify(exactly = 1) { chargeService.chargeSessionState }
        confirmVerified(chargeService)
    }

    @Test
    fun `when invoked then returns same state flow from charge service`() {
        val useCase = getUseCase()
        val result = useCase.invoke()

        assertSame(
            "use case should return same instance from charge service",
            stateFlowMock,
            result,
        )

        verify(exactly = 1) { chargeService.chargeSessionState }
        confirmVerified(chargeService)
    }

    @Test
    fun `when state changes then use case emits same values`() = runTest {
        val useCase = getUseCase()
        val result = useCase()

        val expectedState1 = ChargingSessionState(
            isSessionRunning = true,
            isSessionSummaryReady = false,
            lastSessionData = createTestChargingSession(SessionStatus.CHARGING, 0.15),
        )

        stateFlowMock.value = expectedState1
        assertEquals(expectedState1, result.value)

        val expectedState2 = ChargingSessionState(
            isSessionRunning = false,
            isSessionSummaryReady = true,
            lastSessionData = createTestChargingSession(SessionStatus.STOPPED, 0.30),
        )
        stateFlowMock.value = expectedState2
        assertEquals(expectedState2, result.value)
    }

    private fun getUseCase() = ObserveChargeSessionState(chargeService)
}
