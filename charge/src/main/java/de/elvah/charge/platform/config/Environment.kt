package de.elvah.charge.platform.config

import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow

public sealed class Environment(public val value: String) {
    public object Production : Environment("prod")
    public object Int : Environment("int")
    public class Simulator(public val simulatorFlow: SimulatorFlow) :
        Environment("simulator/${simulatorFlow.name}")
}
