package de.elvah.charge.platform.config

public class Config(
    public val apiKey: String,
    public val darkTheme: Boolean? = null,
    public val environment: Environment = Environment.Int,
) {
    init {
        checkApiKey()
    }

    private fun checkApiKey() {
        if (apiKey.isEmpty()) {
            throw IllegalArgumentException("API key cannot be empty")
        }

        when (environment) {
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
}
