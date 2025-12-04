package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.PaymentConfig
import org.junit.Assert.assertEquals
import org.junit.Test

internal class GetPaymentConfigSettingsTest {

    @Test
    fun `should return payment config from config`() {
        // Given
        val paymentConfig = PaymentConfig(
            googlePayEnabled = true,
            creditCardEnabled = false
        )
        val config = Config(
            apiKey = "evpk_test_12345",
            paymentConfig = paymentConfig
        )
        val useCase = GetPaymentConfigSettings(config)

        // When
        val result = useCase()

        // Then
        assertEquals(paymentConfig, result)
        assertEquals(true, result.googlePayEnabled)
        assertEquals(false, result.creditCardEnabled)
    }

    @Test
    fun `should return default payment config when not specified`() {
        // Given
        val config = Config(apiKey = "evpk_test_12345")
        val useCase = GetPaymentConfigSettings(config)

        // When
        val result = useCase()

        // Then
        assertEquals(true, result.googlePayEnabled)
        assertEquals(true, result.creditCardEnabled)
    }
}