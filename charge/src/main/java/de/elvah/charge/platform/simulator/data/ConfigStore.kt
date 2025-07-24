package de.elvah.charge.platform.simulator.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class ConfigStore {

    /*
    var configs = mutableListOf(
        DefaultSimulatorConfig(
            id = "payment_status",
            title = "",
            value = StringSimulatorValue(""),
            options = emptyList(),
            type = ConfigType.SINGLE_SELECTION
        ),
        DefaultTextSimulatorConfig(
            id = "charge_time",
            title = "Charge session duration",
            value = IntSimulatorValue(0),
            type = ConfigType.TEXT
        ),
        DefaultTextSimulatorConfig(
            id = "charge_time",
            title = "Charge session duration",
            value = BooleanSimulatorValue(false),
            type = ConfigType.TOGGLE
        )
    )

    private val _configs = MutableSharedFlow<List<SimulatorConfig>>()
    val configs : Flow<List<SimulatorConfig>>

    inline fun <T> updateConfig(
        id: String,
        onValueChange: (SimulatorConfigValue<T>) -> SimulatorConfig<T>
    ) {
        val config = configs.first { it.id == id }
        val updatedConfig = onValueChange(config.value as SimulatorConfigValue<T>)
        configs = configs.map {
            if (it.id == id) {
                updatedConfig
            } else {
                it
            }
        } as MutableList<out SimulatorConfig<out Any>>
    }
     */
}

interface SimulatorConfig<T> {
    val id: String
    val title: String
    val value: SimulatorConfigValue<T>
    val type: ConfigType
}

enum class ConfigType {
    TEXT, SINGLE_SELECTION, MULTIPLE_SELECTION, TOGGLE
}

interface SimulatorConfigOptions<T> {
    val options: List<SimulatorConfigValue<T>>
}

interface SimulatorConfigValue<T> {
    val value: T
    val displayValue: String
}

class StringSimulatorValue(override val value: String) : SimulatorConfigValue<String> {
    override val displayValue: String
        get() = value
}

class IntSimulatorValue(override val value: Int) : SimulatorConfigValue<Int> {
    override val displayValue: String
        get() = value.toString()
}

class BooleanSimulatorValue(override val value: Boolean) : SimulatorConfigValue<Boolean> {
    override val displayValue: String
        get() = value.toString()
}


class DefaultSimulatorConfig<T>(
    override val id: String,
    override val title: String,
    override val value: SimulatorConfigValue<T>,
    override val options: List<SimulatorConfigValue<T>>,
    override val type: ConfigType

) : SimulatorConfig<T>, SimulatorConfigOptions<T> {
}


class DefaultTextSimulatorConfig<T>(
    override val id: String,
    override val title: String,
    override val value: SimulatorConfigValue<T>,
    override val type: ConfigType
) : SimulatorConfig<T>
