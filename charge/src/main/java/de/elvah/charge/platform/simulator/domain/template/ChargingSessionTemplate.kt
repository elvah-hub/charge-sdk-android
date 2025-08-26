package de.elvah.charge.platform.simulator.domain.template

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Template Method Pattern: Defines the skeleton of charging session progression.
 * Subclasses can override specific steps while maintaining the overall flow structure.
 */
internal abstract class ChargingSessionTemplate(
    protected val sessionFactory: ChargingSessionFactory
) {

    /**
     * Template method that defines the overall session progression algorithm.
     * This method should not be overridden by subclasses.
     */
    fun processSession(context: SimulationContext): ChargingSession? {
        if (!shouldProcessSession(context)) {
            return context.currentSession
        }

        val session = when {
            isInitialState(context) -> createInitialSession(context)
            isStartingPhase(context) -> handleStartingPhase(context)
            isChargingPhase(context) -> handleChargingPhase(context)
            isStoppingPhase(context) -> handleStoppingPhase(context)
            isFinalPhase(context) -> handleFinalPhase(context)
            else -> handleUnknownState(context)
        }

        return postProcessSession(session, context)
    }

    // Template methods - can be overridden by subclasses

    /**
     * Determines if the session should be processed.
     */
    protected open fun shouldProcessSession(context: SimulationContext): Boolean = true

    /**
     * Checks if this is the initial state (no current session).
     */
    protected fun isInitialState(context: SimulationContext): Boolean =
        context.currentSession == null

    /**
     * Checks if we're in the starting phase.
     */
    protected fun isStartingPhase(context: SimulationContext): Boolean {
        return context.currentStatus in listOf(
            SessionStatus.START_REQUESTED,
            SessionStatus.START_REJECTED
        )
    }

    /**
     * Checks if we're in the charging phase.
     */
    protected fun isChargingPhase(context: SimulationContext): Boolean {
        return context.currentStatus in listOf(SessionStatus.STARTED, SessionStatus.CHARGING)
    }

    /**
     * Checks if we're in the stopping phase.
     */
    protected fun isStoppingPhase(context: SimulationContext): Boolean {
        return context.currentStatus == SessionStatus.STOP_REQUESTED
    }

    /**
     * Checks if we're in the final phase.
     */
    protected fun isFinalPhase(context: SimulationContext): Boolean {
        return context.currentStatus in listOf(SessionStatus.STOPPED, SessionStatus.STOP_REJECTED)
    }

    // Abstract methods - must be implemented by subclasses

    /**
     * Creates the initial session when no session exists.
     */
    protected abstract fun createInitialSession(context: SimulationContext): ChargingSession?

    /**
     * Handles the starting phase of the session.
     */
    protected abstract fun handleStartingPhase(context: SimulationContext): ChargingSession?

    /**
     * Handles the charging phase of the session.
     */
    protected abstract fun handleChargingPhase(context: SimulationContext): ChargingSession?

    /**
     * Handles the stopping phase of the session.
     */
    protected abstract fun handleStoppingPhase(context: SimulationContext): ChargingSession?

    /**
     * Handles the final phase of the session.
     */
    protected abstract fun handleFinalPhase(context: SimulationContext): ChargingSession?

    /**
     * Handles unknown or unexpected states.
     */
    protected open fun handleUnknownState(context: SimulationContext): ChargingSession? {
        return createInitialSession(context)
    }

    /**
     * Post-processes the session after creation.
     * Can be overridden for additional processing.
     */
    protected open fun postProcessSession(
        session: ChargingSession?,
        context: SimulationContext
    ): ChargingSession? {
        return session
    }

    // Helper methods

    /**
     * Creates a session with incremented duration from the current session.
     */
    protected fun incrementDuration(context: SimulationContext): ChargingSession? {
        return context.currentSession?.let { current ->
            sessionFactory.createSession {
                evseId(current.evseId)
                status(current.status)
                consumption(current.consumption)
                duration(current.duration + 3)
                status(current.status1)
            }
        }
    }
}
