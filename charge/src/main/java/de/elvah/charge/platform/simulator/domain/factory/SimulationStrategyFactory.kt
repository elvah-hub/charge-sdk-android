package de.elvah.charge.platform.simulator.domain.factory

import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import de.elvah.charge.platform.simulator.domain.strategy.ChargingSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.CustomSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.DefaultSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.InterruptedChargeSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.StartFailsSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.StartRejectedSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.StopFailsSimulationStrategy
import de.elvah.charge.platform.simulator.domain.strategy.StopRejectedSimulationStrategy

/**
 * Factory Pattern: Creates appropriate simulation strategies based on the flow type.
 * This encapsulates the strategy creation logic and provides a single point of configuration.
 */
internal interface SimulationStrategyFactory {

    /**
     * Creates a simulation strategy based on the provided simulator flow.
     */
    fun createStrategy(flow: SimulatorFlow): ChargingSimulationStrategy
}

/**
 * Default implementation of the SimulationStrategyFactory.
 */
internal class DefaultSimulationStrategyFactory(
    private val sessionFactory: ChargingSessionFactory
) : SimulationStrategyFactory {

    override fun createStrategy(flow: SimulatorFlow): ChargingSimulationStrategy {
        return when (flow) {
            SimulatorFlow.Default -> DefaultSimulationStrategy(sessionFactory)
            SimulatorFlow.StartFails -> StartFailsSimulationStrategy(sessionFactory)
            SimulatorFlow.StopFails -> StopFailsSimulationStrategy(sessionFactory)
            SimulatorFlow.InterruptedCharge -> InterruptedChargeSimulationStrategy(sessionFactory)
            SimulatorFlow.StartRejected -> StartRejectedSimulationStrategy(sessionFactory)
            SimulatorFlow.StopRejected -> StopRejectedSimulationStrategy(sessionFactory)
            is SimulatorFlow.Custom -> CustomSimulationStrategy(
                sessionFactory,
                flow.onSessionStatusUpdate
            )
        }
    }
}
