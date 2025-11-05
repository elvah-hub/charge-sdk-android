package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

/**
 * Default simulation strategy implementing normal charging flow.
 * Progresses through states: START_REQUESTED -> STARTED -> CHARGING -> STOP_REQUESTED -> STOPPED
 */
internal class DefaultSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory
) : ChargingSimulationStrategy {

    override suspend fun generateNextSession(context: SimulationContext): ChargeSession? {
        return when (context.currentStatus) {
            SessionStatus.START_REQUESTED -> {
                if (context.secondsSinceLastChange > 4) {
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

            SessionStatus.START_REJECTED -> {
                context.currentSession?.incrementDuration()
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
        // Default strategy has no internal state to reset
    }

    private fun ChargeSession?.incrementDuration(): ChargeSession? = this?.let {
        ChargeSession(
            evseId = this.evseId,
            status = this.status,
            consumption = this.consumption,
            duration = this.duration + 3,
        )
    }
}
