package de.elvah.charge.platform.simulator.domain.model

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus

/**
 * Context object containing all state information needed for charging simulation.
 * This follows the Context pattern to encapsulate simulation state.
 */
internal data class SimulationContext(
    val currentSession: ChargingSession? = null,
    val sessionCounter: Int = 0,
    val secondsSinceLastChange: Int = 0,
    val bannerRequested: Boolean = false,
    val stopRequested: Boolean = false,
    val simulatorFlow: SimulatorFlow = SimulatorFlow.Default,
    val evseId: String = "DE*KDL*E0000040"
) {
    
    /**
     * Creates a new context with updated session counter.
     */
    fun incrementCounter(): SimulationContext = copy(sessionCounter = sessionCounter + 1)
    
    /**
     * Creates a new context with updated time since last change.
     */
    fun incrementTime(): SimulationContext = copy(secondsSinceLastChange = secondsSinceLastChange + 1)
    
    /**
     * Creates a new context with updated current session.
     */
    fun withSession(session: ChargingSession?): SimulationContext = copy(currentSession = session)
    
    /**
     * Creates a new context with banner requested flag set.
     */
    fun withBannerRequested(): SimulationContext = copy(bannerRequested = true)
    
    /**
     * Creates a new context with stop requested flag set.
     */
    fun withStopRequested(): SimulationContext = copy(stopRequested = true)
    
    /**
     * Resets time-based counters while preserving session state.
     */
    fun resetTimers(): SimulationContext = copy(secondsSinceLastChange = 0)
    
    /**
     * Gets the current session status safely.
     */
    val currentStatus: SessionStatus?
        get() = currentSession?.status1
}