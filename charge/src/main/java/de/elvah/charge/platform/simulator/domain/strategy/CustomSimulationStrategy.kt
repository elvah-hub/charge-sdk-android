package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

internal class CustomSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory,
    private val customLogic: suspend (SimulationContext) -> ChargeSession?
) : ChargingSimulationStrategy {
    override suspend fun generateNextSession(context: SimulationContext): ChargeSession? {
        return customLogic(context)
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
