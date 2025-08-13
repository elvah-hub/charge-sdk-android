package de.elvah.charge.features.adhoc_charging.domain.usecase

import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StartChargingSessionTest {

    private lateinit var chargingRepository: ChargingRepository
    private lateinit var useCase: StartChargingSession

    @Before
    fun setUp() {
        chargingRepository = mockk()
        useCase = StartChargingSession(chargingRepository)
    }

    @Test
    fun `invoke returns success when repository returns success`() = runTest {
        coEvery { chargingRepository.startChargingSession() } returns true.right()
        
        val result = useCase.invoke()
        
        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { success ->
                assertTrue("Expected true", success)
            }
        )
        
        coVerify { chargingRepository.startChargingSession() }
    }

    @Test
    fun `invoke returns false when repository returns false`() = runTest {
        coEvery { chargingRepository.startChargingSession() } returns false.right()
        
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
        coEvery { chargingRepository.startChargingSession() } returns SessionExceptions.OngoingSession.left()
        
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
        coEvery { chargingRepository.startChargingSession() } returns SessionExceptions.GenericError.left()
        
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
        coEvery { chargingRepository.startChargingSession() } returns expectedResult
        
        val actualResult = useCase.invoke()
        
        assertEquals("Result should be passed through unchanged", expectedResult, actualResult)
        coVerify(exactly = 1) { chargingRepository.startChargingSession() }
    }

    @Test
    fun `invoke handles all SessionExceptions types`() = runTest {
        val exceptions = listOf(
            SessionExceptions.OngoingSession,
            SessionExceptions.GenericError
        )
        
        exceptions.forEach { exception ->
            coEvery { chargingRepository.startChargingSession() } returns exception.left()
            
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
        coEvery { chargingRepository.startChargingSession() } returns true.right()
        
        val result = useCase()
        
        assertTrue("Expected Right result", result.isRight())
        coVerify { chargingRepository.startChargingSession() }
    }
}