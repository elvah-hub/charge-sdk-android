package de.elvah.charge.platform.simulator.domain.state

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * State Pattern: Defines the interface for different charging session states.
 * Each state knows how to handle transitions and create the next session.
 */
internal interface ChargingSessionState {

    /**
     * Processes the current state and returns the next session.
     */
    fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession?

    /**
     * Determines the next state based on the current context.
     */
    fun getNextState(context: SimulationContext): ChargingSessionState

    /**
     * Returns the session status this state represents.
     */
    val status: SessionStatus
}

/**
 * Context class that manages the current state and delegates operations to it.
 */
internal class ChargingSessionStateContext(
    private val sessionFactory: ChargingSessionFactory,
    private var currentState: ChargingSessionState = StartRequestedState()
) {

    /**
     * Processes the current state and transitions to the next state if needed.
     */
    fun processSession(context: SimulationContext): ChargingSession? {
        val session = currentState.process(context, sessionFactory)
        currentState = currentState.getNextState(context)
        return session
    }

    /**
     * Gets the current state.
     */
    fun getCurrentState(): ChargingSessionState = currentState

    /**
     * Sets the current state (useful for testing or manual state management).
     */
    fun setState(state: ChargingSessionState) {
        currentState = state
    }

    /**
     * Resets the state machine to the initial state.
     */
    fun reset() {
        currentState = StartRequestedState()
    }
}

/**
 * Initial state - session start has been requested.
 */
internal class StartRequestedState : ChargingSessionState {
    override val status = SessionStatus.START_REQUESTED

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.START_REQUESTED)
            consumption(0.0)
            duration(0)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return when {
            context.sessionCounter > 2 -> StartedState()
            else -> this
        }
    }
}

/**
 * Session has been started but charging hasn't begun yet.
 */
internal class StartedState : ChargingSessionState {
    override val status = SessionStatus.STARTED

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.STARTED)
            consumption(Math.random() + context.sessionCounter)
            duration(context.sessionCounter * 3)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return ChargingState()
    }
}

/**
 * Session is actively charging.
 */
internal class ChargingState : ChargingSessionState {
    override val status = SessionStatus.CHARGING

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.CHARGING)
            consumption(Math.random() + context.secondsSinceLastChange)
            duration(context.secondsSinceLastChange * 3)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return when {
            context.stopRequested -> StopRequestedState()
            else -> this
        }
    }
}

/**
 * Stop has been requested but not yet processed.
 */
internal class StopRequestedState : ChargingSessionState {
    override val status = SessionStatus.STOP_REQUESTED

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.STOP_REQUESTED)
            consumption(Math.random() + context.sessionCounter)
            duration(context.sessionCounter * 3)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return StoppedState()
    }
}

/**
 * Session has been stopped successfully.
 */
internal class StoppedState : ChargingSessionState {
    override val status = SessionStatus.STOPPED

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.STOPPED)
            consumption(context.currentSession?.consumption ?: 0.0)
            duration((context.currentSession?.duration ?: 0) + 3)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return this // Final state
    }
}

/**
 * Start request has been rejected.
 */
internal class StartRejectedState : ChargingSessionState {
    override val status = SessionStatus.START_REJECTED

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.START_REJECTED)
            consumption(context.currentSession?.consumption ?: 0.0)
            duration(context.sessionCounter)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return this // Final state
    }
}

/**
 * Stop request has been rejected.
 */
internal class StopRejectedState : ChargingSessionState {
    override val status = SessionStatus.STOP_REJECTED

    override fun process(
        context: SimulationContext,
        sessionFactory: ChargingSessionFactory
    ): ChargingSession? {
        return sessionFactory.createSession {
            evseId(context.evseId)
            status(SessionStatus.STOP_REJECTED)
            consumption(Math.random() + context.sessionCounter)
            duration(context.sessionCounter * 3)
        }
    }

    override fun getNextState(context: SimulationContext): ChargingSessionState {
        return this // Final state
    }
}
