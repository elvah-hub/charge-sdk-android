package de.elvah.charge.platform.network

import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment

internal class ApiUrlBuilder(private val config: Config) {

    fun url(
        serviceName: String,
        baseDomain: String = "elvah.de",
    ): String {
        val envPrefix = when (config.environment) {
            is Environment.Int -> ".int"
            is Environment.Production -> ""
            is Environment.Simulator -> ".int"
        }

        return "https://$serviceName$envPrefix.$baseDomain"
    }
}
