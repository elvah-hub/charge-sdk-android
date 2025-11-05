package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
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

internal class ObserveChargingSessionTest {

    private lateinit var chargeService: ChargeService
    private lateinit var stateFlowMock: MutableStateFlow<ChargeSession?>

    @Before
    fun setup() {
        stateFlowMock = MutableStateFlow(null)
        chargeService = mockk(relaxed = true)
        every { chargeService.chargeSession } returns stateFlowMock
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

        verify(exactly = 1) { chargeService.chargeSession }
        confirmVerified(chargeService)
    }

    @Test
    fun `when charge state changes then use case flow emits same values`() = runTest {
        val useCase = getUseCase()
        val result = useCase()

        val expected1 = ChargeSession(
            evseId = "123",
            status = SessionStatus.STARTED,
            consumption = 0.15,
            duration = 5,
        )
        stateFlowMock.value = expected1
        assertEquals(expected1, result.value)

        val expected2 = ChargeSession(
            evseId = "123",
            status = SessionStatus.CHARGING,
            consumption = 0.30,
            duration = 8,
        )
        stateFlowMock.value = expected2
        assertEquals(expected2, result.value)
    }

    private fun getUseCase() = ObserveChargingSession(chargeService)
}
