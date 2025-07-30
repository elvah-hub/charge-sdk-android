package de.elvah.charge.platform.config

import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow

sealed class Environment(val value: String) {
    object Production : Environment("prod")
    object Int : Environment("int")
    class Simulator(val simulatorFlow: SimulatorFlow) :
        Environment("simulator/${simulatorFlow.name}")
}
