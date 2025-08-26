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
 * The [SlowDefault] flow is similar to the [Default] flow, but with slower transitions.
 * The [StatusMissing] flow simulates a charging session where the session status is never set.
 *
 *
 * @property name The unique identifier for the flow.
 * @property description A human-readable description of what the flow simulates.
 */
sealed class SimulatorFlow(val name: String, val description: String) {
    object Default : SimulatorFlow("default", "Standard successful charge flow with typical timing")
    object StartFails : SimulatorFlow("startFails", "Simulates start request failures")
    object StartRejected :
        SimulatorFlow("startRejected", "Start request is rejected by the charge point")

    object StopFails : SimulatorFlow("stopFails", "Simulates stop request failures")
    object StopRejected :
        SimulatorFlow("stopRejected", "Stop request is rejected by the charge point")

    object InterruptedCharge :
        SimulatorFlow("interruptedCharge", "Charge session gets unexpectedly interrupted")

    internal data class Custom(
        val onSessionStart: () -> Unit,
        val onSessionStop: () -> Unit,
        internal val onSessionStatusUpdate: (SimulationContext) -> ChargingSession?

    ) : SimulatorFlow("custom", "Custom simulation flow")

    //object SlowDefault : SimulatorFlow("slowDefault", "Similar to default but with slower transitions")

    //object StatusMissing : SimulatorFlow("statusMissing", "Session status is never set")
}
