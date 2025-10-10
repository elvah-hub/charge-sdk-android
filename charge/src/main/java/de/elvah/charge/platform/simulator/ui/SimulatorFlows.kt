package de.elvah.charge.platform.simulator.ui

internal enum class SimulatorFlows(val value: String, val description: String) {
    DEFAULT("default", "Standard successful charge flow with typical timing"),
    START_FAILS("startFails", "Simulates start request failures"),
    START_REJECTED("startRejected", "Start request is rejected by the charge point"),
    STOP_FAILS("stopFails", "Simulates stop request failures"),
    STOP_REJECTED("stopRejected", "Stop request is rejected by the charge point"),
    INTERRUPTED_CHARGE("interruptedCharge", "Charge session gets unexpectedly interrupted"),
    SLOW_DEFAULT("slowDefault", "Similar to default but with slower transitions"),
    STATUS_MISSING("statusMissing", "Session status is never set");
}

