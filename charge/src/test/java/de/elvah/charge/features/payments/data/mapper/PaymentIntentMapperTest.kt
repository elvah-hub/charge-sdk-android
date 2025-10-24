package de.elvah.charge.features.payments.data.mapper

import de.elvah.charge.features.payments.data.remote.model.OrganisationDetailsDto
import de.elvah.charge.features.payments.data.remote.model.response.AuthorisationAmount
import de.elvah.charge.features.payments.data.remote.model.response.CreatePaymentIntentResponse
import de.elvah.charge.features.payments.data.remote.model.response.Data
import org.junit.Assert.assertEquals
import org.junit.Test

class PaymentIntentMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val response = createPaymentIntentResponse(
            paymentIntentId = "pi_test_123456",
            accountId = "acct_test_789",
            paymentId = "pay_test_456",
            clientSecret = "pi_test_123456_secret_xyz",
            amountValue = 25.50,
            currency = "EUR"
        )

        val domain = response.toDomain()

        assertEquals("pi_test_123456", domain.paymentIntentId)
        assertEquals("acct_test_789", domain.accountId)
        assertEquals("pay_test_456", domain.paymentId)
        assertEquals("pi_test_123456_secret_xyz", domain.clientSecret)
        assertEquals(25.50, domain.amount, 0.001)
        assertEquals("EUR", domain.currency)
    }

    @Test
    fun `toDomain handles zero amount correctly`() {
        val response = createPaymentIntentResponse(
            paymentIntentId = "pi_zero_amount",
            accountId = "acct_zero",
            paymentId = "pay_zero",
            clientSecret = "secret_zero",
            amountValue = 0.0,
            currency = "USD"
        )

        val domain = response.toDomain()

        assertEquals(0.0, domain.amount, 0.001)
        assertEquals("USD", domain.currency)
    }

    @Test
    fun `toDomain handles large amount correctly`() {
        val response = createPaymentIntentResponse(
            paymentIntentId = "pi_large_amount",
            accountId = "acct_large",
            paymentId = "pay_large",
            clientSecret = "secret_large",
            amountValue = 999.99,
            currency = "GBP"
        )

        val domain = response.toDomain()

        assertEquals(999.99, domain.amount, 0.001)
        assertEquals("GBP", domain.currency)
    }

    @Test
    fun `toDomain handles different currency codes`() {
        val currencies = listOf("EUR", "USD", "GBP", "CHF", "SEK", "NOK", "DKK")

        currencies.forEach { currency ->
            val response = createPaymentIntentResponse(
                paymentIntentId = "pi_currency_test",
                accountId = "acct_currency",
                paymentId = "pay_currency",
                clientSecret = "secret_currency",
                amountValue = 10.0,
                currency = currency
            )

            val domain = response.toDomain()

            assertEquals("Currency mapping failed for $currency", currency, domain.currency)
        }
    }

    @Test
    fun `toDomain maps nested authorization amount fields correctly`() {
        val response = createPaymentIntentResponse(
            paymentIntentId = "pi_nested_test",
            accountId = "acct_nested",
            paymentId = "pay_nested",
            clientSecret = "secret_nested",
            amountValue = 42.75,
            currency = "EUR"
        )

        val domain = response.toDomain()

        assertEquals("Nested amount value not mapped correctly", 42.75, domain.amount, 0.001)
        assertEquals("Nested currency not mapped correctly", "EUR", domain.currency)
    }

    @Test
    fun `toDomain handles fractional amounts with precision`() {
        val response = createPaymentIntentResponse(
            paymentIntentId = "pi_precision_test",
            accountId = "acct_precision",
            paymentId = "pay_precision",
            clientSecret = "secret_precision",
            amountValue = 12.345,
            currency = "EUR"
        )

        val domain = response.toDomain()

        assertEquals(12.345, domain.amount, 0.0001)
    }

    private fun createPaymentIntentResponse(
        paymentIntentId: String,
        accountId: String,
        paymentId: String,
        clientSecret: String,
        amountValue: Double,
        currency: String
    ) = CreatePaymentIntentResponse(
        data = Data(
            paymentIntentId = paymentIntentId,
            accountId = accountId,
            paymentId = paymentId,
            clientSecret = clientSecret,
            authorisationAmount = AuthorisationAmount(
                value = amountValue,
                currency = currency
            ),
            organisationDetails = OrganisationDetailsDto(
                companyName = "Test CPO",
                logoUrl = "https://example.com/logo.png",
                privacyUrl = "https://example.com/privacy",
                termsOfConditionUrl = "https://example.com/terms",
                supportContacts = emptyList()
            )
        )
    )
}
