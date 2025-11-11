package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SummaryInfo
import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.factory.DefaultChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.factory.DefaultSimulationStrategyFactory
import de.elvah.charge.platform.simulator.domain.factory.SimulationStrategyFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import de.elvah.charge.platform.simulator.domain.strategy.ChargingSimulationStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

internal class FakeChargingRepository(
    private val chargingStore: ChargingStore,
    private val config: Config,
    private val sessionFactory: ChargingSessionFactory = DefaultChargingSessionFactory(),
    private val strategyFactory: SimulationStrategyFactory = DefaultSimulationStrategyFactory(
        sessionFactory
    )
) : ChargingRepository {

    private val currentContext = MutableStateFlow(SimulationContext())

    private var currentStrategy: ChargingSimulationStrategy? = null

    override suspend fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails) {
        chargingStore.saveOrganisationDetails(organisationDetails)
    }

    override suspend fun getAdditionalCosts(): AdditionalCosts? {
        return chargingStore.getAdditionalCosts()
    }

    override suspend fun storeAdditionalCosts(additionalCosts: AdditionalCosts?) {
        chargingStore.storeAdditionalCosts(additionalCosts)
    }

    override suspend fun fetchChargingSession(): Either<Exception, ChargeSession> {
        return try {
            val context = currentContext.value

            if (currentStrategy == null) {
                initializeStrategy()
            }

            if (!context.bannerRequested) {
                updateContext { it.withBannerRequested() }
                return NullPointerException("Banner not requested yet").left()
            }

            val session = currentStrategy?.generateNextSession(context)

            updateContext { ctx ->
                ctx.incrementCounter()
                    .incrementTime()
                    .withSession(session)
            }

            session?.right() ?: NullPointerException("No session generated").left()

        } catch (exception: Exception) {
            exception.left()
        }
    }

    override suspend fun startChargingSession(): Either<SessionExceptions, Boolean> {
        val simulatorFlow = getCurrentSimulatorFlow()
        return if (simulatorFlow != SimulatorFlow.StartFails) {
            true.right()
        } else {
            SessionExceptions.GenericError.left()
        }
    }

    override suspend fun stopChargingSession(): Either<SessionExceptions, Boolean> {
        updateContext { it.withStopRequested() }

        val simulatorFlow = getCurrentSimulatorFlow()
        return if (simulatorFlow != SimulatorFlow.StopFails) {
            true.right()
        } else {
            SessionExceptions.GenericError.left()
        }
    }

    override suspend fun getSummary(): SummaryInfo? {
        return chargingStore.getChargingPrefs().first().let {
            SummaryInfo(
                paymentId = it.paymentId,
                logoUrl = it.logoUrl,
            )
        }
    }

    override suspend fun resetSession() {
        chargingStore.resetSession()
        resetSimulation()
    }

    /**
     * Initializes the simulation strategy based on current configuration.
     */
    private fun initializeStrategy() {
        val simulatorFlow = getCurrentSimulatorFlow()
        currentStrategy = strategyFactory.createStrategy(simulatorFlow)

        updateContext { it.copy(simulatorFlow = simulatorFlow) }
    }

    /**
     * Gets the current simulator flow from configuration.
     */
    private fun getCurrentSimulatorFlow(): SimulatorFlow {
        return (config.environment as? Environment.Simulator)?.simulatorFlow
            ?: SimulatorFlow.Default
    }

    /**
     * Updates the simulation context in a thread-safe manner.
     */
    private fun updateContext(update: (SimulationContext) -> SimulationContext) {
        currentContext.update(update)
    }

    /**
     * Resets the simulation to initial state.
     * Useful for testing or when switching simulation scenarios.
     */
    fun resetSimulation() {
        currentContext.value = SimulationContext()
        currentStrategy?.reset()
        currentStrategy = null
    }

    /**
     * Manually sets a specific simulation strategy.
     * Useful for testing or custom configurations.
     */
    fun setSimulationStrategy(strategy: ChargingSimulationStrategy) {
        currentStrategy = strategy
    }

    /**
     * Gets the current simulation context (for testing/debugging).
     */
    fun getCurrentContext(): SimulationContext = currentContext.value

    /**
     * Checks if the current simulation should continue running.
     */
    fun shouldContinueSimulation(): Boolean {
        return currentStrategy?.shouldContinue(currentContext.value) ?: true
    }
}
