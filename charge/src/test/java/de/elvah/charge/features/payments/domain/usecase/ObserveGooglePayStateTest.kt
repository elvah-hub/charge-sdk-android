package de.elvah.charge.features.payments.domain.usecase

import app.cash.turbine.test
import de.elvah.charge.features.payments.domain.manager.GooglePayManager
import de.elvah.charge.features.payments.domain.model.GooglePayState
import de.elvah.charge.features.payments.domain.model.isAvailable
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class ObserveGooglePayStateTest {

    private lateinit var googlePayManager: GooglePayManager
    private lateinit var observeGooglePayState: ObserveGooglePayState

    @Before
    fun setup() {
        googlePayManager = GooglePayManager()
        observeGooglePayState = ObserveGooglePayState(googlePayManager)
    }

    // Happy Path Tests

    @Test
    fun `should return initial Unavailable state from manager`() = runTest {
        // When
        val stateFlow = observeGooglePayState()

        // Then
        stateFlow.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Unavailable, state)
            assertFalse(state.isAvailable)
        }
    }

    @Test
    fun `should emit Idle state when Google Pay becomes available`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            val updatedState = awaitItem()

            assertEquals(GooglePayState.Idle, updatedState)
            assertTrue(updatedState.isAvailable)
        }
    }

    @Test
    fun `should emit updated state when payment state changes`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            awaitItem() // Idle state

            googlePayManager.setProcessingState()
            val processingState = awaitItem()
            assertEquals(GooglePayState.Processing, processingState)

            googlePayManager.processPaymentResult(GooglePayState.Success)
            val successState = awaitItem()
            assertEquals(GooglePayState.Success, successState)
        }
    }

    @Test
    fun `should emit state for complete payment flow`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            val initialState = awaitItem()
            assertEquals(GooglePayState.Unavailable, initialState)
            assertFalse(initialState.isAvailable)

            googlePayManager.setGooglePayAvailable(true)
            val availableState = awaitItem()
            assertEquals(GooglePayState.Idle, availableState)
            assertTrue(availableState.isAvailable)

            googlePayManager.setProcessingState()
            val processingState = awaitItem()
            assertEquals(GooglePayState.Processing, processingState)

            googlePayManager.processPaymentResult(GooglePayState.Success)
            val successState = awaitItem()
            assertEquals(GooglePayState.Success, successState)

            googlePayManager.resetPaymentState()
            val resetState = awaitItem()
            assertEquals(GooglePayState.Idle, resetState)
            assertTrue(resetState.isAvailable) // availability preserved
        }
    }

    // Unhappy Path Tests

    @Test
    fun `should emit failed state with error message when available`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()
        val errorMessage = "Network error"

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            awaitItem() // Idle state

            googlePayManager.processPaymentResult(GooglePayState.Failed(errorMessage))
            val failedState = awaitItem()

            assertTrue(failedState is GooglePayState.Failed)
            assertEquals(errorMessage, (failedState as GooglePayState.Failed).error)
        }
    }

    @Test
    fun `should not emit failed state when unavailable`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()
        val errorMessage = "Network error"

        // When & Then
        stateFlow.test {
            val initialState = awaitItem() // Initial Unavailable state

            googlePayManager.processPaymentResult(GooglePayState.Failed(errorMessage))

            // Should not emit anything - state remains Unavailable
            expectNoEvents()
        }
    }

    @Test
    fun `should emit cancelled state when available`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            awaitItem() // Idle state

            googlePayManager.processPaymentResult(GooglePayState.Cancelled)
            val cancelledState = awaitItem()

            assertEquals(GooglePayState.Cancelled, cancelledState)
        }
    }

    @Test
    fun `should emit Unavailable state when Google Pay becomes unavailable`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            val availableState = awaitItem()
            assertTrue(availableState.isAvailable)

            googlePayManager.setGooglePayAvailable(false)
            val unavailableState = awaitItem()
            assertEquals(GooglePayState.Unavailable, unavailableState)
            assertFalse(unavailableState.isAvailable)
        }
    }

    // Edge Cases

    @Test
    fun `should handle multiple consecutive availability changes`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            val idle1 = awaitItem()
            assertEquals(GooglePayState.Idle, idle1)

            googlePayManager.setGooglePayAvailable(false)
            val unavailable = awaitItem()
            assertEquals(GooglePayState.Unavailable, unavailable)

            googlePayManager.setGooglePayAvailable(true)
            val idle2 = awaitItem()
            assertEquals(GooglePayState.Idle, idle2)
        }
    }

    @Test
    fun `should preserve current state when setting available while already available`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            awaitItem() // Idle state

            googlePayManager.setProcessingState()
            val processing = awaitItem()
            assertEquals(GooglePayState.Processing, processing)

            googlePayManager.setGooglePayAvailable(true)
            // Should not emit - state preserved
            expectNoEvents()
        }
    }

    @Test
    fun `should handle rapid state changes`() = runTest {
        // Given
        val stateFlow = observeGooglePayState()

        // When & Then
        stateFlow.test {
            awaitItem() // Initial Unavailable state

            // Rapid changes
            googlePayManager.setGooglePayAvailable(true)
            googlePayManager.setProcessingState()
            googlePayManager.processPaymentResult(GooglePayState.Success)

            val idle = awaitItem()
            assertEquals(GooglePayState.Idle, idle)

            val processing = awaitItem()
            assertEquals(GooglePayState.Processing, processing)

            val success = awaitItem()
            assertEquals(GooglePayState.Success, success)
        }
    }

    @Test
    fun `should handle multiple observers`() = runTest {
        // Given
        val stateFlow1 = observeGooglePayState()
        val stateFlow2 = observeGooglePayState()

        // When
        googlePayManager.setGooglePayAvailable(true)

        // Then - both flows should emit the same updated value
        stateFlow1.test {
            val state1 = awaitItem()
            assertEquals(GooglePayState.Idle, state1)

            stateFlow2.test {
                val state2 = awaitItem()
                assertEquals(GooglePayState.Idle, state2)
                assertEquals(state1, state2)
            }
        }
    }

    @Test
    fun `should return same StateFlow instance on multiple invocations`() {
        // When
        val stateFlow1 = observeGooglePayState()
        val stateFlow2 = observeGooglePayState()

        // Then - both should reference the same underlying flow from manager
        assertEquals(stateFlow1, stateFlow2)
    }
}
