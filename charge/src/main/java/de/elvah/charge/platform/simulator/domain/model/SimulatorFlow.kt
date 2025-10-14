package de.elvah.charge.platform.simulator.domain.model

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession

/**
 * Represents the different simulation flows that can be executed.
 * Each flow defines a specific scenario for a charging session.
 *
 * For example:
 * the [Default] flow simulates a successful charging session with typical timing.
 * The [StartFails] flow simulates a failure to start the charging session.
 * The [StopFails] flow simulates a failure to stop the charging session.
 * The [InterruptedCharge] flow simulates a charging session that gets interrupted unexpectedly.
 * The [StopRejected] flow simulates a charging session where the stop request is rejected by the charge point.
 * The [StartRejected] flow simulates a charging session where the start request is rejected by the charge point.
 *
 * @property name The unique identifier for the flow.
 * @property description A human-readable description of what the flow simulates.
 */
public sealed class SimulatorFlow(public val name: String, public val description: String) {
    public object Default :
        SimulatorFlow("default", "Standard successful charge flow with typical timing")

    public object StartFails : SimulatorFlow("startFails", "Simulates start request failures")
    public object StartRejected :
        SimulatorFlow("startRejected", "Start request is rejected by the charge point")

    public object StopFails : SimulatorFlow("stopFails", "Simulates stop request failures")
    public object StopRejected :
        SimulatorFlow("stopRejected", "Stop request is rejected by the charge point")

    public object InterruptedCharge :
        SimulatorFlow("interruptedCharge", "Charge session gets unexpectedly interrupted")

    internal data class Custom(
        val onSessionStart: () -> Unit,
        val onSessionStop: () -> Unit,
        internal val onSessionStatusUpdate: (SimulationContext) -> ChargingSession?

    ) : SimulatorFlow("custom", "Custom simulation flow")
}
