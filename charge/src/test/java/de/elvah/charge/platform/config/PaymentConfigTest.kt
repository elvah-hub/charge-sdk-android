package de.elvah.charge.platform.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class PaymentConfigTest {

    @Test
    fun `PaymentConfig with both payment methods enabled should succeed`() {
        // When creating config with both methods enabled
        val config = PaymentConfig(
            googlePayEnabled = true,
            creditCardEnabled = true
        )
        
        // Then config should be created successfully
        assertTrue(config.googlePayEnabled)
        assertTrue(config.creditCardEnabled)
    }

    @Test
    fun `PaymentConfig with only Google Pay enabled should succeed`() {
        // When creating config with only Google Pay enabled
        val config = PaymentConfig(
            googlePayEnabled = true,
            creditCardEnabled = false
        )
        
        // Then config should be created successfully
        assertTrue(config.googlePayEnabled)
        assertFalse(config.creditCardEnabled)
    }

    @Test
    fun `PaymentConfig with only credit card enabled should succeed`() {
        // When creating config with only credit card enabled
        val config = PaymentConfig(
            googlePayEnabled = false,
            creditCardEnabled = true
        )
        
        // Then config should be created successfully
        assertFalse(config.googlePayEnabled)
        assertTrue(config.creditCardEnabled)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `PaymentConfig with both payment methods disabled should throw exception`() {
        // When creating config with both methods disabled
        // Then it should throw IllegalArgumentException
        PaymentConfig(
            googlePayEnabled = false,
            creditCardEnabled = false
        )
    }

    @Test
    fun `PaymentConfig default values should have both methods enabled`() {
        // When creating config with default values
        val config = PaymentConfig()
        
        // Then both payment methods should be enabled by default
        assertTrue(config.googlePayEnabled)
        assertTrue(config.creditCardEnabled)
    }
}