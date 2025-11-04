package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

internal class StopChargingSessionTest {

    private lateinit var chargeService: ChargeService

    @Before
    fun setup() {
        chargeService = mockk(relaxed = true)
    }

    @Test
    fun `when invoked then stop session method is called on charge service`() {
        val useCase = getUseCase()
        useCase.invoke()
        verify(exactly = 1) { chargeService.stopSession() }
        confirmVerified(chargeService)
    }

    @Test
    fun `when not invoked then stop session method is not called on charge service`() {
        getUseCase()
        verify(exactly = 0) { chargeService.stopSession() }
        confirmVerified(chargeService)
    }

    private fun getUseCase() = StopChargingSession(chargeService)
}
