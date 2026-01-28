package de.elvah.charge.features.payments.domain.manager

import app.cash.turbine.test
import de.elvah.charge.features.payments.domain.model.GooglePayState
import de.elvah.charge.features.payments.domain.model.isAvailable
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

internal class GooglePayManagerTest {

    private lateinit var googlePayManager: GooglePayManager

    @Before
    fun setup() {
        googlePayManager = GooglePayManager()
    }

    // Happy Path Tests

    @Test
    fun `should start with Unavailable state`() = runTest {
        // When
        val state = googlePayManager.state.value

        // Then
        assertEquals(GooglePayState.Unavailable, state)
        assertFalse(state.isAvailable)
    }

    @Test
    fun `should transition to Idle when becoming available`() = runTest {
        // When
        googlePayManager.setGooglePayAvailable(true)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Idle, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should set processing state when setProcessingState is called and available`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)

        // When
        googlePayManager.setProcessingState()

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Processing, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should not set processing state when unavailable`() = runTest {
        // Given - state is Unavailable

        // When
        googlePayManager.setProcessingState()

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Unavailable, state)
            assertFalse(state.isAvailable)
        }
    }

    @Test
    fun `should process success payment result when available`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)

        // When
        googlePayManager.processPaymentResult(GooglePayState.Success)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Success, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should not process payment result when unavailable`() = runTest {
        // Given - state is Unavailable

        // When
        googlePayManager.processPaymentResult(GooglePayState.Success)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Unavailable, state)
        }
    }

    @Test
    fun `should reset to Idle when resetPaymentState is called and available`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)
        googlePayManager.setProcessingState()

        // When
        googlePayManager.resetPaymentState()

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Idle, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should not reset when unavailable`() = runTest {
        // Given - state is Unavailable

        // When
        googlePayManager.resetPaymentState()

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Unavailable, state)
        }
    }

    // Unhappy Path Tests

    @Test
    fun `should process failed payment result with error message when available`() = runTest {
        // Given
        val errorMessage = "Payment declined"
        googlePayManager.setGooglePayAvailable(true)

        // When
        googlePayManager.processPaymentResult(GooglePayState.Failed(errorMessage))

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertTrue(state is GooglePayState.Failed)
            assertEquals(errorMessage, (state as GooglePayState.Failed).error)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should process cancelled payment result when available`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)
        googlePayManager.setProcessingState()

        // When
        googlePayManager.processPaymentResult(GooglePayState.Cancelled)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Cancelled, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should transition to Unavailable when setGooglePayAvailable false`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)

        // When
        googlePayManager.setGooglePayAvailable(false)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Unavailable, state)
            assertFalse(state.isAvailable)
        }
    }

    // Edge Cases

    @Test
    fun `should preserve current state when becoming available if not Unavailable`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)
        googlePayManager.setProcessingState()

        // When
        googlePayManager.setGooglePayAvailable(true)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Processing, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should handle multiple state transitions correctly`() = runTest {
        // When - simulate a complete payment flow
        googlePayManager.setGooglePayAvailable(true)
        googlePayManager.setProcessingState()
        googlePayManager.processPaymentResult(GooglePayState.Success)
        googlePayManager.resetPaymentState()

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Idle, state)
            assertTrue(state.isAvailable)
        }
    }

    @Test
    fun `should handle failed payment with empty error message`() = runTest {
        // Given
        googlePayManager.setGooglePayAvailable(true)

        // When
        googlePayManager.processPaymentResult(GooglePayState.Failed(""))

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertTrue(state is GooglePayState.Failed)
            assertEquals("", (state as GooglePayState.Failed).error)
        }
    }

    @Test
    fun `should emit new state value when state changes`() = runTest {
        // Given
        val states = mutableListOf<GooglePayState>()

        // When
        googlePayManager.state.test {
            states.add(awaitItem()) // Initial Unavailable state

            googlePayManager.setGooglePayAvailable(true)
            states.add(awaitItem()) // Idle state

            googlePayManager.setProcessingState()
            states.add(awaitItem()) // Processing state

            cancelAndIgnoreRemainingEvents()
        }

        // Then
        assertEquals(3, states.size)
        assertEquals(GooglePayState.Unavailable, states[0])
        assertEquals(GooglePayState.Idle, states[1])
        assertEquals(GooglePayState.Processing, states[2])
    }

    @Test
    fun `should remain Unavailable when toggling availability quickly`() = runTest {
        // When
        googlePayManager.setGooglePayAvailable(false)
        googlePayManager.setGooglePayAvailable(false)

        // Then
        googlePayManager.state.test {
            val state = awaitItem()
            assertEquals(GooglePayState.Unavailable, state)
        }
    }
}
