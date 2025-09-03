package de.elvah.charge.platform.config

import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConfigTest {

    @Test
    fun `Config creates successfully with valid test API key in Int environment`() {
        val config = Config(
            apiKey = "evpk_test_1234567890abcdef",
            environment = Environment.Int
        )
        
        assertEquals("evpk_test_1234567890abcdef", config.apiKey)
        assertEquals(Environment.Int, config.environment)
        assertNull(config.darkTheme)
    }

    @Test
    fun `Config creates successfully with valid production API key in Production environment`() {
        val config = Config(
            apiKey = "evpk_prod_1234567890abcdef",
            environment = Environment.Production
        )
        
        assertEquals("evpk_prod_1234567890abcdef", config.apiKey)
        assertEquals(Environment.Production, config.environment)
        assertNull(config.darkTheme)
    }

    @Test
    fun `Config creates successfully with darkTheme parameter`() {
        val config = Config(
            apiKey = "evpk_test_1234567890abcdef",
            darkTheme = true,
            environment = Environment.Int
        )
        
        assertEquals(true, config.darkTheme)
    }

    @Test
    fun `Config creates successfully in Simulator environment with any API key`() {
        val config = Config(
            apiKey = "any_key_works_in_simulator",
            environment = Environment.Simulator(SimulatorFlow.Default)
        )
        
        assertEquals("any_key_works_in_simulator", config.apiKey)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Config throws exception when API key is empty`() {
        Config(
            apiKey = "",
            environment = Environment.Int
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Config throws exception when test API key used in Production environment`() {
        Config(
            apiKey = "evpk_test_1234567890abcdef",
            environment = Environment.Production
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Config throws exception when production API key used in Int environment`() {
        Config(
            apiKey = "evpk_prod_1234567890abcdef",
            environment = Environment.Int
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Config throws exception when API key does not start with evpk_test in Int environment`() {
        Config(
            apiKey = "invalid_test_key_format",
            environment = Environment.Int
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Config throws exception when API key does not start with evpk_prod in Production environment`() {
        Config(
            apiKey = "invalid_prod_key_format",
            environment = Environment.Production
        )
    }

    @Test
    fun `Config throws correct error message for empty API key`() {
        try {
            Config(
                apiKey = "",
                environment = Environment.Int
            )
        } catch (e: IllegalArgumentException) {
            assertEquals("API key cannot be empty", e.message)
        }
    }

    @Test
    fun `Config throws correct error message for production key in Int environment`() {
        try {
            Config(
                apiKey = "evpk_prod_1234567890abcdef",
                environment = Environment.Int
            )
        } catch (e: IllegalArgumentException) {
            assertEquals("API Key error: You are using a production API key", e.message)
        }
    }

    @Test
    fun `Config throws correct error message for test key in Production environment`() {
        try {
            Config(
                apiKey = "evpk_test_1234567890abcdef",
                environment = Environment.Production
            )
        } catch (e: IllegalArgumentException) {
            assertEquals("API Key error: You are using a test API key", e.message)
        }
    }

    @Test
    fun `Config throws correct error message for invalid test key format in Int environment`() {
        try {
            Config(
                apiKey = "invalid_test_key_format",
                environment = Environment.Int
            )
        } catch (e: IllegalArgumentException) {
            assertEquals("API key must start with evpk_test", e.message)
        }
    }

    @Test
    fun `Config throws correct error message for invalid production key format in Production environment`() {
        try {
            Config(
                apiKey = "invalid_prod_key_format",
                environment = Environment.Production
            )
        } catch (e: IllegalArgumentException) {
            assertEquals("API key must start with evpk_prod", e.message)
        }
    }

    @Test
    fun `Config accepts minimal valid test API key`() {
        val config = Config(
            apiKey = "evpk_test",
            environment = Environment.Int
        )
        
        assertEquals("evpk_test", config.apiKey)
    }

    @Test
    fun `Config accepts minimal valid production API key`() {
        val config = Config(
            apiKey = "evpk_prod",
            environment = Environment.Production
        )
        
        assertEquals("evpk_prod", config.apiKey)
    }

    @Test
    fun `Config defaults to Int environment when not specified`() {
        val config = Config(apiKey = "evpk_test_default")
        
        assertEquals(Environment.Int, config.environment)
    }

    @Test
    fun `Config accepts long API keys with special characters for test environment`() {
        val longApiKey = "evpk_test_1234567890abcdef-ghijklmnop_QRSTUVWXYZ.special~chars"
        val config = Config(
            apiKey = longApiKey,
            environment = Environment.Int
        )
        
        assertEquals(longApiKey, config.apiKey)
    }

    @Test
    fun `Config accepts long API keys with special characters for production environment`() {
        val longApiKey = "evpk_prod_1234567890abcdef-ghijklmnop_QRSTUVWXYZ.special~chars"
        val config = Config(
            apiKey = longApiKey,
            environment = Environment.Production
        )
        
        assertEquals(longApiKey, config.apiKey)
    }
}
