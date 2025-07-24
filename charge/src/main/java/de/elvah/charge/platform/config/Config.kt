package de.elvah.charge.platform.config

import de.elvah.charge.Environment

internal class Config(
    val apiKey: String,
    val darkTheme: Boolean? = null,
    val environment: Environment = Environment.Int,
)
