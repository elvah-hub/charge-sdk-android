package de.elvah.charge.features.adhoc_charging.data.service

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import de.elvah.charge.common.createTestChargingSession
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeServiceState
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ElvahChargeServiceTest {

    private val dispatcher = StandardTestDispatcher()
    private val chargeTestScope = TestScope(dispatcher)

    private val getPaymentSummary = mockk<GetPaymentSummary>()

    private fun getChargeService(
        chargingRepository: ChargingRepository = mockk<DefaultChargingRepository>(),
        checkOnInit: Boolean = true,
    ): ChargeService = ElvahChargeService(
        lifecycle = null,
        chargingRepository = chargingRepository,
        getPaymentSummary = getPaymentSummary,
        chargeScope = chargeTestScope,
        checkOnInit = checkOnInit,
    )

    @Test
    fun `stop session if during polling session status is stopped`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.startChargingSession() } returns true.right()

        val charging = createTestChargingSession(SessionStatus.CHARGING, 10.0)
        val charging2 = createTestChargingSession(SessionStatus.CHARGING, 20.0)
        val stopped = createTestChargingSession(SessionStatus.STOPPED, 30.0)

        coEvery { chargingRepository.fetchChargingSession() } returnsMany listOf(
            charging.right(),
            charging2.right(),
            stopped.right(),
        )

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.startSession()

        chargeService.chargeSession.test {
            chargeTestScope.advanceTimeBy(1.seconds)

            // skip default initial value
            skipItems(1)
            chargeTestScope.advanceTimeBy(1.seconds)

            val firstItem = awaitItem()
            assertEquals(charging, firstItem)
            assertEquals(true, chargeService.isPolling)
            assertNotNull(chargeService.pollingJob)

            chargeTestScope.advanceTimeBy(1.seconds)

            val secondItem = awaitItem()
            assertEquals(charging2, secondItem)

            chargeTestScope.advanceTimeBy(1.seconds)

            val thirdItem = awaitItem()
            assertEquals(stopped, thirdItem)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(false, chargeService.isPolling)
        assertNull(chargeService.pollingJob)
    }

    @Test
    fun `requesting stop session that fails should emit error state`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        val sessionException = SessionExceptions.GenericError
        coEvery { chargingRepository.stopChargingSession() } returns sessionException.left()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.stopSession()

        chargeService.state.test {
            assertEquals(ChargeServiceState.STOPPING, awaitItem())
            chargeTestScope.advanceTimeBy(1.seconds)
            assertEquals(ChargeServiceState.IDLE, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        chargeService.errors.test {
            assertEquals(ChargeError.StopAttemptFailed(sessionException), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requesting stop session should emit summary state`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.stopChargingSession() } returns true.right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.stopSession()

        chargeService.state.test {
            val stopping = awaitItem()
            assertEquals(ChargeServiceState.STOPPING, stopping)
            chargeTestScope.advanceTimeBy(1.seconds)

            val summary = awaitItem()
            assertEquals(ChargeServiceState.SUMMARY, summary)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requesting stop session should stop polling`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.stopChargingSession() } returns true.right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.stopSession()
        chargeTestScope.advanceUntilIdle()

        assertEquals(false, chargeService.isPolling)
        assertNull(chargeService.pollingJob)
    }

    @Test
    fun `requesting start session that fails should emit error state`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        val sessionException = SessionExceptions.GenericError
        coEvery { chargingRepository.startChargingSession() } returns sessionException.left()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.startSession()

        chargeService.state.test {
            assertEquals(ChargeServiceState.STARTING, awaitItem())
            chargeTestScope.advanceTimeBy(1.seconds)
            assertEquals(ChargeServiceState.IDLE, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        chargeService.errors.test {
            assertEquals(ChargeError.StartAttemptFailed(sessionException), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requesting start session should emit started state`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.startChargingSession() } returns true.right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.startSession()

        chargeService.state.test {
            assertEquals(ChargeServiceState.STARTING, awaitItem())
            chargeTestScope.advanceTimeBy(1.seconds)
            assertEquals(ChargeServiceState.STARTED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `requesting start session should start polling`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.startChargingSession() } returns true.right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
            checkOnInit = false,
        ) as ElvahChargeService

        chargeService.startSession()
        chargeTestScope.advanceUntilIdle()

        assertEquals(true, chargeService.isPolling)
        assertNotNull(chargeService.pollingJob)
    }

    @Test
    fun `confirm polling starts emit charge states`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.fetchChargingSession() } returns ChargeSession(
            evseId = "1",
            status = SessionStatus.STARTED,
            consumption = 0.5,
            duration = 5,
        ).right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
        ) as ElvahChargeService

        chargeService.state.test {
            assertEquals(ChargeServiceState.VERIFYING, awaitItem())
            chargeTestScope.advanceTimeBy(1.minutes)
            assertEquals(ChargeServiceState.STARTED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(true, chargeService.isPolling)
        assertNotNull(chargeService.pollingJob)

        val expectedCharge1 = createTestChargingSession(SessionStatus.CHARGING, 0.10)
        val expectedCharge2 = createTestChargingSession(SessionStatus.CHARGING, 0.15)
        val expectedCharge3 = createTestChargingSession(SessionStatus.CHARGING, 0.20)
        val expectedCharge4 = createTestChargingSession(SessionStatus.CHARGING, 0.25)

        coEvery { chargingRepository.fetchChargingSession() } returnsMany listOf(
            expectedCharge1.right(),
            expectedCharge2.right(),
            expectedCharge3.right(),
            expectedCharge4.right(),
        )

        chargeService.chargeSession.test {
            // ignore emission of first active session check (STARTED)
            skipItems(1)
            chargeTestScope.advanceTimeBy(1.minutes)

            val result1 = awaitItem().right().getOrNull()
            assertEquals(expectedCharge1, result1)

            val result2 = awaitItem().right().getOrNull()
            assertEquals(expectedCharge2, result2)

            val result3 = awaitItem().right().getOrNull()
            assertEquals(expectedCharge3, result3)

            val result4 = awaitItem().right().getOrNull()
            assertEquals(expectedCharge4, result4)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirm polling starts on initial check when exists an active session`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.fetchChargingSession() } returns ChargeSession(
            evseId = "1",
            status = SessionStatus.STARTED,
            consumption = 0.5,
            duration = 5,
        ).right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
        ) as ElvahChargeService

        chargeService.state.test {
            assertEquals(ChargeServiceState.VERIFYING, awaitItem())
            chargeTestScope.advanceTimeBy(1.minutes)
            assertEquals(ChargeServiceState.STARTED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(true, chargeService.isPolling)
        assertNotNull(chargeService.pollingJob)
    }

    @Test
    fun `confirm started state on service start with initial check`() = runTest {
        val chargingRepository = mockk<DefaultChargingRepository>()

        coEvery { chargingRepository.fetchChargingSession() } returns ChargeSession(
            evseId = "1",
            status = SessionStatus.STARTED,
            consumption = 0.5,
            duration = 5,
        ).right()

        val chargeService = getChargeService(
            chargingRepository = chargingRepository,
        )

        chargeService.state.test {
            assertEquals(ChargeServiceState.VERIFYING, awaitItem())
            chargeTestScope.advanceTimeBy(1.seconds)
            assertEquals(ChargeServiceState.STARTED, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirm verifying state on service start with initial check`() = runTest {
        val chargeService = getChargeService()

        chargeService.state.test {
            assertEquals(ChargeServiceState.VERIFYING, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirm idle state on service start without initial check`() = runTest {
        val chargeService = getChargeService(
            checkOnInit = false,
        )

        chargeService.state.test {
            assertEquals(ChargeServiceState.IDLE, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
