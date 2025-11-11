package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError
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

internal class ObserveChargeServiceErrorsTest {

    private lateinit var chargeService: ChargeService
    private lateinit var stateFlowMock: MutableStateFlow<ChargeError?>

    @Before
    fun setup() {
        stateFlowMock = MutableStateFlow(null)
        chargeService = mockk(relaxed = true)
        every { chargeService.errors } returns stateFlowMock
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

        verify(exactly = 1) { chargeService.errors }
        confirmVerified(chargeService)
    }

    @Test
    fun `when error changes then use case flow emits same values`() = runTest {
        val useCase = getUseCase()
        val result = useCase()

        val expectedStartedState = ChargeError.StartAttemptFailed(SessionExceptions.OngoingSession)
        stateFlowMock.value = expectedStartedState
        assertEquals(expectedStartedState, result.value)

        val expectedGenericError = ChargeError.GenericError
        stateFlowMock.value = expectedGenericError
        assertEquals(expectedGenericError, result.value)
    }

    private fun getUseCase() = ObserveChargeServiceErrors(chargeService)
}
