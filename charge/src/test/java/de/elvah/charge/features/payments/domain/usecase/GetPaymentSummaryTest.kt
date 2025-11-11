package de.elvah.charge.features.payments.domain.usecase

import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetPaymentSummaryTest {

    private lateinit var paymentsRepository: PaymentsRepository
    private lateinit var useCase: GetPaymentSummary

    @Before
    fun setUp() {
        paymentsRepository = mockk()
        useCase = GetPaymentSummary(paymentsRepository)
    }

    @Test
    fun `invoke returns success when repository returns success`() = runTest {
        val paymentId = "payment_123"
        val paymentSummary = createTestPaymentSummary()
        coEvery { paymentsRepository.getPaymentSummary(paymentId) } returns paymentSummary.right()

        val result = useCase.invoke(paymentId)

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { summary ->
                assertEquals("DE*KDL*E0000040", summary.evseId)
                assertEquals("Test CPO", summary.cpoName)
                assertEquals(2550, summary.totalCost)
            }
        )

        coVerify { paymentsRepository.getPaymentSummary(paymentId) }
    }

    @Test
    fun `invoke returns failure when repository returns failure`() = runTest {
        val paymentId = "payment_456"
        val exception = RuntimeException("Payment summary not found")
        coEvery { paymentsRepository.getPaymentSummary(paymentId) } returns exception.left()

        val result = useCase.invoke(paymentId)

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
        val paymentId = "payment_789"
        val paymentSummary = createTestPaymentSummary()
        val expectedResult = paymentSummary.right()
        coEvery { paymentsRepository.getPaymentSummary(paymentId) } returns expectedResult

        val actualResult = useCase.invoke(paymentId)

        assertEquals("Result should be passed through unchanged", expectedResult, actualResult)
        coVerify(exactly = 1) { paymentsRepository.getPaymentSummary(paymentId) }
    }

    private fun createTestPaymentSummary() = PaymentSummary(
        evseId = "DE*KDL*E0000040",
        cpoName = "Test CPO",
        logoUrl = "",
        address = "Test Address",
        totalTime = 120,
        consumedKWh = 25.5,
        totalCost = 2550
    )
}
