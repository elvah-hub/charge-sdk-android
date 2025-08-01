package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Simulation strategy that simulates a charging session where stop is rejected.
 * The session starts and charges normally, but when stop is requested, it gets rejected.
 */
internal class StopRejectedSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory,
    private val startDelay: Int = 2
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
                if (context.stopRequested) {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STOP_REQUESTED)
                        consumption(Math.random() + context.secondsSinceLastChange)
                        duration(context.secondsSinceLastChange * 3)
                    }
                } else {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.CHARGING)
                        consumption(Math.random() + context.secondsSinceLastChange)
                        duration(context.secondsSinceLastChange * 3)
                    }

                }
            }

            SessionStatus.STOP_REQUESTED -> {
                // This is where the stop gets rejected
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
