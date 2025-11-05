package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Simulation strategy that simulates an interrupted charging session.
 * The session charges normally for a while, then gets automatically interrupted.
 */
internal class InterruptedChargeSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory,
    private val interruptionThreshold: Int = 8,
    private val startDelay: Int = 2
) : ChargingSimulationStrategy {

    override suspend fun generateNextSession(context: SimulationContext): ChargeSession? {
        return when (context.currentStatus) {
            null -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.START_REQUESTED)
                    consumption(0.0)
                    duration(0)
                }
            }

            SessionStatus.START_REQUESTED -> {
                if (context.sessionCounter > startDelay) {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STARTED)
                        consumption(context.currentSession?.consumption ?: 0.0)
                        duration(context.sessionCounter)
                    }
                } else {
                    context.currentSession
                }
            }

            SessionStatus.STARTED -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.CHARGING)
                    consumption(0.0)
                    duration(0)
                }
            }

            SessionStatus.CHARGING -> {
                if (context.secondsSinceLastChange < interruptionThreshold) {
                    // Continue charging normally
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.CHARGING)
                        consumption(Math.random() + context.secondsSinceLastChange)
                        duration(context.secondsSinceLastChange * 3)
                    }
                } else {
                    // Interrupt the session by automatically requesting stop
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STOP_REQUESTED)
                        consumption(Math.random() + context.secondsSinceLastChange)
                        duration(context.secondsSinceLastChange * 3)
                    }
                }
            }

            SessionStatus.STOP_REQUESTED -> {
                // After interruption, the stop also fails
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.STOP_REJECTED)
                    consumption(Math.random() + context.sessionCounter)
                    duration(context.sessionCounter * 3)
                }
            }

            else -> {
                context.currentSession
            }
        }
    }

    override fun shouldContinue(context: SimulationContext): Boolean {
        return context.currentStatus != SessionStatus.STOP_REJECTED
    }

    override fun reset() {
        // This strategy has no internal state to reset
    }
}
