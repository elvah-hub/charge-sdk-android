package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Strategy Pattern: Defines the interface for all charging simulation scenarios.
 * Each concrete strategy implements a specific simulation behavior.
 */
internal interface ChargingSimulationStrategy {

    /**
     * Generates the next charging session state based on the current context.
     *
     * @param context Current simulation state and configuration
     * @return Next charging session or null if session should end
     */
    suspend fun generateNextSession(context: SimulationContext): ChargeSession?

    /**
     * Determines if the simulation should continue running.
     */
    fun shouldContinue(context: SimulationContext): Boolean

    /**
     * Resets the strategy to its initial state.
     */
    fun reset()
}
