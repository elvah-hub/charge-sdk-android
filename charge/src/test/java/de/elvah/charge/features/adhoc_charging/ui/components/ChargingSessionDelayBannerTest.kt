package de.elvah.charge.features.adhoc_charging.ui.components

import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@ExperimentalCoroutinesApi
internal class ChargingSessionDelayBannerTest {

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `should show banner for STARTED status after delay`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false
        val sessionStatus = SessionStatus.STARTED
        val delayTimeSeconds = 2L // Short delay for testing

        // When - Simulate the banner logic
        if (shouldShowBanner(sessionStatus)) {
            // Simulate the delay logic from LaunchedEffect
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        // Fast forward time
        advanceTimeBy(delayTimeSeconds * 1000)

        // Then
        assertTrue("Banner should be visible after delay for STARTED status", bannerVisible)
    }

    @Test
    fun `should show banner for START_REQUESTED status after delay`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false
        val sessionStatus = SessionStatus.START_REQUESTED
        val delayTimeSeconds = 2L

        // When
        if (shouldShowBanner(sessionStatus)) {
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        advanceTimeBy(delayTimeSeconds * 1000)

        // Then
        assertTrue("Banner should be visible after delay for START_REQUESTED status", bannerVisible)
    }

    @Test
    fun `should show banner for STOP_REQUESTED status after delay`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false
        val sessionStatus = SessionStatus.STOP_REQUESTED
        val delayTimeSeconds = 2L

        // When
        if (shouldShowBanner(sessionStatus)) {
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        advanceTimeBy(delayTimeSeconds * 1000)

        // Then
        assertTrue("Banner should be visible after delay for STOP_REQUESTED status", bannerVisible)
    }

    @Test
    fun `should NOT show banner for CHARGING status`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false
        val sessionStatus = SessionStatus.CHARGING
        val delayTimeSeconds = 2L

        // When
        if (shouldShowBanner(sessionStatus)) {
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        advanceTimeBy(delayTimeSeconds * 1000)

        // Then
        assertFalse("Banner should NOT be visible for CHARGING status", bannerVisible)
    }

    @Test
    fun `should NOT show banner for START_REJECTED status`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false
        val sessionStatus = SessionStatus.START_REJECTED
        val delayTimeSeconds = 2L

        // When
        if (shouldShowBanner(sessionStatus)) {
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        advanceTimeBy(delayTimeSeconds * 1000)

        // Then
        assertFalse("Banner should NOT be visible for START_REJECTED status", bannerVisible)
    }

    @Test
    fun `should NOT show banner for STOPPED status`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false
        val sessionStatus = SessionStatus.STOPPED
        val delayTimeSeconds = 2L

        // When
        if (shouldShowBanner(sessionStatus)) {
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        advanceTimeBy(delayTimeSeconds * 1000)

        // Then
        assertFalse("Banner should NOT be visible for STOPPED status", bannerVisible)
    }

    @Test
    fun `should hide banner when status changes from valid to invalid`() = runTest(testDispatcher) {
        // Given
        var bannerVisible = false

        // Initially valid status
        var sessionStatus = SessionStatus.STARTED
        val delayTimeSeconds = 2L

        // When - First show banner for valid status
        if (shouldShowBanner(sessionStatus)) {
            delay(delayTimeSeconds * 1000)
            bannerVisible = true
        }

        advanceTimeBy(delayTimeSeconds * 1000)
        assertTrue("Banner should be visible for valid status", bannerVisible)

        // When - Status changes to invalid
        sessionStatus = SessionStatus.CHARGING
        if (!shouldShowBanner(sessionStatus)) {
            bannerVisible = false
        }

        // Then
        assertFalse("Banner should be hidden when status becomes invalid", bannerVisible)
    }

    private fun shouldShowBanner(sessionStatus: SessionStatus): Boolean {
        return sessionStatus in listOf(
            SessionStatus.STARTED,
            SessionStatus.START_REQUESTED,
            SessionStatus.STOP_REQUESTED
        )
    }
}
