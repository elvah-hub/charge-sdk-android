package de.elvah.charge.platform.config

internal object ChargeConfig {
    lateinit var config: Config

    fun initialize(config: Config) {
        this.config = config
    }
}