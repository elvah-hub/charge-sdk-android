package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Simulation strategy for Start Requested Delayed scenario.
 * Stays in START_REQUESTED state for more than 30 seconds to trigger delayed banner.
 */
internal class StartRequestedDelayedSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory
) : ChargingSimulationStrategy {

    override suspend fun generateNextSession(context: SimulationContext): ChargingSession? {
        return when (context.currentStatus) {
            SessionStatus.START_REQUESTED -> {
                // Stay in START_REQUESTED for more than 30 seconds (35 seconds)
                if (context.secondsSinceLastChange > 35) {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STARTED)
                        consumption(0.0)
                        duration(0)
                    }
                } else {
                    context.currentSession?.incrementDuration()
                }
            }

            SessionStatus.STARTED -> {
                if (context.secondsSinceLastChange > 4) {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.CHARGING)
                        consumption(0.0)
                        duration(0)
                    }
                } else {
                    context.currentSession?.incrementDuration()
                }
            }

            SessionStatus.CHARGING -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.CHARGING)
                    consumption(Math.random() + context.sessionCounter)
                    duration(context.sessionCounter * 3)
                }
            }

            SessionStatus.STOP_REQUESTED -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.STOPPED)
                    consumption(Math.random() + context.sessionCounter)
                    duration(context.sessionCounter * 3)
                }
            }

            SessionStatus.STOPPED -> {
                context.currentSession?.incrementDuration()
            }

            else -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.START_REQUESTED)
                    consumption(0.0)
                    duration(0)
                }
            }
        }
    }

    override fun shouldContinue(context: SimulationContext): Boolean {
        return context.currentStatus != SessionStatus.STOPPED || !context.stopRequested
    }

    override fun reset() {
        // No internal state to reset
    }

    private fun ChargingSession?.incrementDuration(): ChargingSession? = this?.let {
        ChargingSession(
            evseId = this.evseId,
            status = this.status,
            consumption = this.consumption,
            duration = this.duration + 3,
        )
    }
}