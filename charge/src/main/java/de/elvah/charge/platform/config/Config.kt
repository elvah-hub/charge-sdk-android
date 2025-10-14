package de.elvah.charge.platform.config

import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow

public class Config(
    public val apiKey: String,
    public val darkTheme: Boolean? = null,
    environment: Environment? = null,
) {
    public val environment: Environment = environment ?: detectEnvironmentFromApiKey(apiKey)

    init {
        validateApiKey()
    }

    private fun validateApiKey() {
        if (apiKey.isEmpty()) {
            throw IllegalArgumentException("API key cannot be empty")
        }

        when (this.environment) {
            Environment.Int -> if (!apiKey.startsWith("evpk_test")) {
                if (apiKey.startsWith("evpk_prod")) {
                    throw IllegalArgumentException("API Key error: You are using a production API key")
                } else {
                    throw IllegalArgumentException("API key must start with evpk_test")
                }
            }

            Environment.Production -> {
                if (!apiKey.startsWith("evpk_prod")) {
                    if (apiKey.startsWith("evpk_test")) {
                        throw IllegalArgumentException("API Key error: You are using a test API key")
                    } else {
                        throw IllegalArgumentException("API key must start with evpk_prod")
                    }
                }
            }

            else -> {}
        }
    }

    private fun detectEnvironmentFromApiKey(apiKey: String): Environment {
        return when {
            apiKey.startsWith("evpk_test") -> Environment.Int
            apiKey.startsWith("evpk_prod") -> Environment.Production
            else -> Environment.Simulator(SimulatorFlow.Default) // Default fallback
        }
    }
}
