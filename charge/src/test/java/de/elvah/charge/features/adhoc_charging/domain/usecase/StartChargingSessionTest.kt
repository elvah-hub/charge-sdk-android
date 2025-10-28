package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import io.mockk.mockk
import org.junit.Before

class StartChargingSessionTest {

    private lateinit var chargeService: ChargeService
    private lateinit var useCase: StartChargingSession

    @Before
    fun setUp() {
        chargeService = mockk()
        useCase = StartChargingSession(chargeService)
    }

    /*@Test
    fun `invoke returns success when repository returns success`() = runTest {
        coEvery { chargeService.state } returns flowOf(
            ChargeState.STARTING,
            ChargeState.STARTED,
        )

        useCase.invoke()

        chargeService.state.test {
            assertEquals(ChargeState.STARTING, awaitItem())
            assertEquals(ChargeState.STARTED, awaitItem())
        }

        // assertTrue("Expected Right result", result.isRight())
        // fail("Expected success but got failure: $it")
        // assertTrue("Expected true", success)
    }*/
/* TODO: fix tests
    @Test
    fun `invoke returns false when repository returns false`() = runTest {
        coEvery { chargeService.startSession() } returns false.right()

        val result = useCase.invoke()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { success ->
                assertFalse("Expected false", success)
            }
        )
    }

    @Test
    fun `invoke returns OngoingSession error when repository returns OngoingSession`() = runTest {
        coEvery { chargeService.startSession() } returns SessionExceptions.OngoingSession.left()

        val result = useCase.invoke()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(SessionExceptions.OngoingSession, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `invoke returns GenericError when repository returns GenericError`() = runTest {
        coEvery { chargeService.startSession() } returns SessionExceptions.GenericError.left()

        val result = useCase.invoke()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(SessionExceptions.GenericError, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `invoke delegates to repository without modification`() = runTest {
        val expectedResult = true.right()
        coEvery {  } returns expectedResult

        chargeService.startSession()

        val actualResult = useCase.invoke()

        assertEquals("Result should be passed through unchanged", expectedResult, actualResult)
        coVerify(exactly = 1) { chargeService.startSession() }
    }
 chargeService*/

}
