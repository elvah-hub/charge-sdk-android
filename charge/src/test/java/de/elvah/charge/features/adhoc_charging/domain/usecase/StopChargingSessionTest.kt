package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import io.mockk.mockk
import org.junit.Before

class StopChargingSessionTest {

    private lateinit var chargeService: ChargeService
    private lateinit var useCase: StopChargingSession

    @Before
    fun setUp() {
        chargeService = mockk()
        useCase = StopChargingSession(chargeService)
    }
/* TODO: fix tests
    @Test
    fun `invoke returns success when repository returns success`() = runTest {
        coEvery { chargeService.stopSessionOld() } returns true.right()

        val result = useCase.invoke()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { success ->
                assertTrue("Expected true", success)
            }
        )

        coVerify { chargeService.stopSessionOld() }
    }

    @Test
    fun `invoke returns false when repository returns false`() = runTest {
        coEvery { chargeService.stopSessionOld() } returns false.right()

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
        coEvery { chargeService.stopSessionOld() } returns SessionExceptions.OngoingSession.left()

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
        coEvery { chargeService.stopSessionOld() } returns SessionExceptions.GenericError.left()

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
        coEvery { chargeService.stopSessionOld() } returns expectedResult

        val actualResult = useCase.invoke()

        assertEquals("Result should be passed through unchanged", expectedResult, actualResult)
        coVerify(exactly = 1) { chargeService.stopSessionOld() }
    }

    @Test
    fun `invoke handles all SessionExceptions types`() = runTest {
        val exceptions = listOf(
            SessionExceptions.OngoingSession,
            SessionExceptions.GenericError
        )

        exceptions.forEach { exception ->
            coEvery { chargeService.stopSessionOld() } returns exception.left()

            val result = useCase.invoke()

            assertTrue("Expected Left result for $exception", result.isLeft())
            result.fold(
                ifLeft = { error ->
                    assertEquals("Expected $exception", exception, error)
                },
                ifRight = { fail("Expected failure but got success for $exception") }
            )
        }
    }

    @Test
    fun `invoke is a suspend function`() = runTest {
        coEvery { chargeService.stopSessionOld() } returns true.right()

        val result = useCase()

        assertTrue("Expected Right result", result.isRight())
        coVerify { chargeService.stopSessionOld() }
    }
 */
}
