package de.elvah.charge.features.adhoc_charging.domain.usecase

import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetActiveChargingSessionTest {

    private lateinit var chargingRepository: ChargingRepository
    private lateinit var useCase: GetActiveChargingSession

    @Before
    fun setUp() {
        chargingRepository = mockk()
        useCase = GetActiveChargingSession(chargingRepository)
    }

    @Test
    fun `invoke returns success when repository returns success`() = runTest {
        val chargingSession = createTestChargingSession()
        coEvery { chargingRepository.fetchChargingSession() } returns chargingSession.right()

        val result = useCase.invoke()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { session ->
                assertEquals("DE*KDL*E0000040", session.evseId)
                assertEquals(15.5, session.consumption, 0.001)
                assertEquals(120, session.duration)
                assertEquals(SessionStatus.CHARGING, session.status)
            }
        )

        coVerify { chargingRepository.fetchChargingSession() }
    }

    @Test
    fun `invoke returns failure when repository returns failure`() = runTest {
        val exception = RuntimeException("Fetch session failed")
        coEvery { chargingRepository.fetchChargingSession() } returns exception.left()

        val result = useCase.invoke()

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(exception, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `invoke delegates to repository without modification`() = runTest {
        val chargingSession = createTestChargingSession()
        val expectedResult = chargingSession.right()
        coEvery { chargingRepository.fetchChargingSession() } returns expectedResult

        val actualResult = useCase.invoke()

        assertEquals("Result should be passed through unchanged", expectedResult, actualResult)
        coVerify(exactly = 1) { chargingRepository.fetchChargingSession() }
    }

    @Test
    fun `invoke handles different exception types`() = runTest {
        val exceptions = listOf(
            RuntimeException("Runtime error"),
            IllegalStateException("Illegal state"),
            NullPointerException("Null pointer"),
            Exception("Generic exception")
        )

        exceptions.forEach { exception ->
            coEvery { chargingRepository.fetchChargingSession() } returns exception.left()

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
    fun `invoke handles different session states`() = runTest {
        val sessionStates = listOf(
            SessionStatus.START_REQUESTED,
            SessionStatus.STARTED,
            SessionStatus.CHARGING,
            SessionStatus.STOP_REQUESTED,
            SessionStatus.STOPPED,
            SessionStatus.START_REJECTED,
            SessionStatus.STOP_REJECTED
        )

        sessionStates.forEach { status ->
            val session = ChargingSession(
                evseId = "DE*TEST*E000001",
                status = status,
                consumption = 10.0,
                duration = 60,
            )

            coEvery { chargingRepository.fetchChargingSession() } returns session.right()

            val result = useCase.invoke()

            assertTrue("Expected Right result for $status", result.isRight())
            result.fold(
                ifLeft = { fail("Expected success but got failure for $status") },
                ifRight = { returnedSession ->
                    assertEquals("Session status mismatch", status, returnedSession.status)
                }
            )
        }
    }

    @Test
    fun `invoke is a suspend function`() = runTest {
        val chargingSession = createTestChargingSession()
        coEvery { chargingRepository.fetchChargingSession() } returns chargingSession.right()

        val result = useCase()

        assertTrue("Expected Right result", result.isRight())
        coVerify { chargingRepository.fetchChargingSession() }
    }

    @Test
    fun `invoke handles zero consumption and duration`() = runTest {
        val session = ChargingSession(
            evseId = "DE*ZERO*E000000",
            consumption = 0.0,
            duration = 0,
            status = SessionStatus.START_REQUESTED
        )
        coEvery { chargingRepository.fetchChargingSession() } returns session.right()

        val result = useCase.invoke()

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure") },
            ifRight = { returnedSession ->
                assertEquals(0.0, returnedSession.consumption, 0.001)
                assertEquals(0, returnedSession.duration)
            }
        )
    }

    private fun createTestChargingSession() = ChargingSession(
        evseId = "DE*KDL*E0000040",
        consumption = 15.5,
        duration = 120,
        status = SessionStatus.CHARGING
    )
}
