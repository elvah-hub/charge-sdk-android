package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeServiceState
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

internal class ObserveChargeServiceStateTest {

    private lateinit var chargeService: ChargeService
    private lateinit var stateFlowMock: MutableStateFlow<ChargeServiceState>

    @Before
    fun setup() {
        stateFlowMock = MutableStateFlow(ChargeServiceState.IDLE)
        chargeService = mockk(relaxed = true)
        every { chargeService.state } returns stateFlowMock
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

        verify(exactly = 1) { chargeService.state }
        confirmVerified(chargeService)
    }

    @Test
    fun `when charge state changes then use case flow emits same values`() = runTest {
        val useCase = getUseCase()
        val result = useCase()

        val expectedStartedState = ChargeServiceState.STARTED
        stateFlowMock.value = expectedStartedState
        assertEquals(expectedStartedState, result.value)

        val expectedSummaryState = ChargeServiceState.SUMMARY
        stateFlowMock.value = expectedSummaryState
        assertEquals(expectedSummaryState, result.value)
    }

    private fun getUseCase() = ObserveChargeServiceState(chargeService)
}
