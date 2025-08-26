package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Simulation strategy that simulates a charging session where start fails.
 * After several attempts, the session will be rejected.
 */
internal class StartFailsSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory,
    private val failureThreshold: Int = 3
) : ChargingSimulationStrategy {

    override suspend fun generateNextSession(context: SimulationContext): ChargingSession? {
        return when (context.currentStatus) {
            null -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.START_REQUESTED)
                    consumption(0.0)
                    duration(0)
                }
            }

            SessionStatus.START_REQUESTED,
            SessionStatus.START_REJECTED -> {
                if (context.sessionCounter > failureThreshold) {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.START_REJECTED)
                        consumption(context.currentSession?.consumption ?: 0.0)
                        duration(context.sessionCounter)
                    }
                } else {
                    context.currentSession
                }
            }

            else -> {
                context.currentSession
            }
        }
    }

    override fun shouldContinue(context: SimulationContext): Boolean {
        return context.currentStatus != SessionStatus.START_REJECTED ||
                context.sessionCounter <= failureThreshold
    }

    override fun reset() {
        // This strategy has no internal state to reset
    }
}
