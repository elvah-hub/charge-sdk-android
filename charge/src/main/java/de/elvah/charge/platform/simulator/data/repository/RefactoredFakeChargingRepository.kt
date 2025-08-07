package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.simulator.domain.factory.ChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.factory.DefaultChargingSessionFactory
import de.elvah.charge.platform.simulator.domain.factory.DefaultSimulationStrategyFactory
import de.elvah.charge.platform.simulator.domain.factory.SimulationStrategyFactory
import de.elvah.charge.platform.simulator.domain.model.SimulationContext
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import de.elvah.charge.platform.simulator.domain.strategy.ChargingSimulationStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

/**
 * Refactored FakeChargingRepository using Gang of Four design patterns.
 * 
 * Applied Patterns:
 * - Strategy Pattern: Different simulation strategies for various scenarios
 * - Factory Pattern: Creation of ChargingSession objects and strategies
 * - Builder Pattern: Flexible session configuration through ChargingSessionBuilder
 * - Template Method Pattern: Common session progression logic (used within strategies)
 * - State Pattern: Session status management (alternative to Strategy, can be used together)
 * 
 * Benefits:
 * - Single Responsibility: Each class has one clear purpose
 * - Open/Closed: Easy to add new simulation scenarios without modifying existing code
 * - Dependency Inversion: Depends on abstractions, not concrete implementations
 * - Better testability: Each component can be tested in isolation
 * - Reduced code duplication: Common logic is centralized
 * - Configuration flexibility: Easy to configure different behaviors
 */
internal class RefactoredFakeChargingRepository(
    private val chargingStore: ChargingStore,
    private val sessionFactory: ChargingSessionFactory = DefaultChargingSessionFactory(),
    private val strategyFactory: SimulationStrategyFactory = DefaultSimulationStrategyFactory(sessionFactory)
) : ChargingRepository {

    // Reactive state management
    private val _activeSessions: MutableSharedFlow<ChargingSession?> = MutableSharedFlow()
    override val activeSessions: Flow<ChargingSession?>
        get() = _activeSessions.asSharedFlow()

    // Simulation state - encapsulated in context object
    private val currentContext = MutableStateFlow(SimulationContext())
    
    // Current strategy - determined by configuration
    private var currentStrategy: ChargingSimulationStrategy? = null

    override suspend fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails) {
        chargingStore.saveOrganisationDetails(organisationDetails)
    }

    override suspend fun fetchChargingSession(): Either<Exception, ChargingSession> {
        return try {
            val context = currentContext.value
            
            // Initialize strategy if needed
            if (currentStrategy == null) {
                initializeStrategy()
            }
            
            // Handle banner request logic
            if (!context.bannerRequested) {
                updateContext { it.withBannerRequested() }
                return NullPointerException("Banner not requested yet").left()
            }
            
            // Generate next session using current strategy
            val session = currentStrategy?.generateNextSession(context)
            
            // Update context and emit session
            updateContext { ctx ->
                ctx.incrementCounter()
                    .incrementTime()
                    .withSession(session)
            }
            
            // Emit to reactive stream
            _activeSessions.emit(session)
            
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
        // Update context to mark stop as requested
        updateContext { it.withStopRequested() }
        
        val simulatorFlow = getCurrentSimulatorFlow()
        return if (simulatorFlow != SimulatorFlow.StopFails) {
            true.right()
        } else {
            SessionExceptions.GenericError.left()
        }
    }
    
    /**
     * Initializes the simulation strategy based on current configuration.
     */
    private fun initializeStrategy() {
        val simulatorFlow = getCurrentSimulatorFlow()
        currentStrategy = strategyFactory.createStrategy(simulatorFlow)
        
        // Update context with current flow
        updateContext { it.copy(simulatorFlow = simulatorFlow) }
    }
    
    /**
     * Gets the current simulator flow from configuration.
     */
    private fun getCurrentSimulatorFlow(): SimulatorFlow {
        return (ChargeConfig.config.environment as? Environment.Simulator)?.simulatorFlow 
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