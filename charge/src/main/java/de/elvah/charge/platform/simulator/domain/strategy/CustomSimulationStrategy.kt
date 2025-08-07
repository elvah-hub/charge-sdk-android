package de.elvah.charge.platform.simulator.domain.strategy

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext

internal class CustomSimulationStrategy(
    private val sessionFactory: ChargingSessionFactory,
    private val customLogic: suspend (SimulationContext) -> ChargingSession?
) : ChargingSimulationStrategy {
    override suspend fun generateNextSession(context: SimulationContext): ChargingSession? {
        return customLogic(context)
    }

    override fun shouldContinue(context: SimulationContext): Boolean {
        return context.currentStatus != SessionStatus.STOPPED || !context.stopRequested
    }

    override fun reset() {
        // Default strategy has no internal state to reset
    }

    private fun ChargingSession?.incrementDuration(): ChargingSession? = this?.let {
        ChargingSession(
            evseId = this.evseId,
            status = this.status,
            consumption = this.consumption,
            duration = this.duration + 3,
            status1 = this.status1
        )
    }
}
