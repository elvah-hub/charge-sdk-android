# Simulator Examples

This document provides practical examples for using the Elvah Charge SDK simulator in various testing scenarios.

## Table of Contents

- [Basic Examples](#basic-examples)
- [Custom Strategy Examples](#custom-strategy-examples)
- [Testing Patterns](#testing-patterns)
- [Real-World Scenarios](#real-world-scenarios)

## Basic Examples

### Example 1: Testing Happy Path

```kotlin
@Test
fun `test successful charging flow`() = runTest {
    // Configure for default successful flow
    val config = ChargeConfig(
        environment = Environment.Simulator(
            simulatorFlow = SimulatorFlow.Default
        )
    )
    
    val repository = RefactoredFakeChargingRepository(mockChargingStore)
    val sessionStates = mutableListOf<SessionStatus>()
    
    // Collect session updates
    val job = launch {
        repository.activeSessions
            .filterNotNull()
            .collect { session ->
                sessionStates.add(session.status1)
            }
    }
    
    // Simulate user interactions
    repository.fetchChargingSession() // Banner request
    repository.fetchChargingSession() // Start request
    repository.startChargingSession() // User starts
    
    repeat(3) {
        repository.fetchChargingSession() // Charging progress
    }
    
    repository.stopChargingSession() // User stops
    repository.fetchChargingSession() // Final state
    
    job.cancel()
    
    // Verify expected flow
    assertThat(sessionStates).containsExactly(
        SessionStatus.START_REQUESTED,
        SessionStatus.STARTED,
        SessionStatus.CHARGING,
        SessionStatus.CHARGING,
        SessionStatus.CHARGING,
        SessionStatus.STOPPED
    )
}
```

### Example 2: Testing Error Scenarios

```kotlin
@Test
fun `test start failure handling`() = runTest {
    val config = ChargeConfig(
        environment = Environment.Simulator(
            simulatorFlow = SimulatorFlow.StartFails
        )
    )
    
    val repository = RefactoredFakeChargingRepository(mockChargingStore)
    
    // Attempt to start charging
    val startResult = repository.startChargingSession()
    
    // Should return error for StartFails flow
    assertThat(startResult.isLeft()).isTrue()
    
    // Get the final session state
    repository.fetchChargingSession()
    val finalSession = repository.activeSessions.first()
    
    assertThat(finalSession?.status1).isEqualTo(SessionStatus.START_REJECTED)
}
```

## Custom Strategy Examples

### Example 1: Time-Based Charging

```kotlin
class TimeBasedChargingExample {
    
    fun createTimeBasedStrategy(): SimulatorFlow.Custom {
        return SimulatorFlow.Custom(
            onSessionStart = { println("Starting time-based charging") },
            onSessionStop = { println("Stopping time-based charging") },
            onSessionStatusUpdate = { context ->
                createTimeBasedSession(context)
            }
        )
    }
    
    private fun createTimeBasedSession(context: SimulationContext): ChargingSession? {
        val sessionFactory = DefaultChargingSessionFactory()
        val currentTime = System.currentTimeMillis()
        
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
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.CHARGING)
                    consumption(0.0)
                    duration(0)
                }
            }
            
            SessionStatus.CHARGING -> {
                val chargingTimeSeconds = context.secondsSinceLastChange
                val maxChargingTime = 300 // 5 minutes
                
                if (chargingTimeSeconds >= maxChargingTime || context.stopRequested) {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STOPPED)
                        consumption(chargingTimeSeconds * 0.12) // 0.12 kWh per second
                        duration(chargingTimeSeconds)
                    }
                } else {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.CHARGING)
                        consumption(chargingTimeSeconds * 0.12)
                        duration(chargingTimeSeconds)
                    }
                }
            }
            
            else -> context.currentSession
        }
    }
}

// Usage
@Test
fun `test time-based charging`() = runTest {
    val example = TimeBasedChargingExample()
    val customFlow = example.createTimeBasedStrategy()
    
    val config = ChargeConfig(
        environment = Environment.Simulator(simulatorFlow = customFlow)
    )
    
    val repository = RefactoredFakeChargingRepository(mockChargingStore)
    
    // Start charging and let it run for simulated time
    repository.fetchChargingSession() // Start
    
    repeat(10) {
        delay(100) // Simulate time passing
        repository.fetchChargingSession()
    }
    
    val finalSession = repository.activeSessions.first()
    assertThat(finalSession?.consumption).isGreaterThan(0.0)
}
```

### Example 2: Battery Level Simulation

```kotlin
class BatteryLevelSimulation {
    
    fun createBatteryBasedStrategy(initialBatteryLevel: Double = 20.0): SimulatorFlow.Custom {
        var currentBatteryLevel = initialBatteryLevel
        
        return SimulatorFlow.Custom(
            onSessionStart = { println("Starting battery-based charging at ${currentBatteryLevel}%") },
            onSessionStop = { println("Stopping battery-based charging at ${currentBatteryLevel}%") },
            onSessionStatusUpdate = { context ->
                createBatteryBasedSession(context, currentBatteryLevel) { newLevel ->
                    currentBatteryLevel = newLevel
                }
            }
        )
    }
    
    private fun createBatteryBasedSession(
        context: SimulationContext,
        batteryLevel: Double,
        onBatteryUpdate: (Double) -> Unit
    ): ChargingSession? {
        val sessionFactory = DefaultChargingSessionFactory()
        
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
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.CHARGING)
                    consumption(0.0)
                    duration(0)
                }
            }
            
            SessionStatus.CHARGING -> {
                val chargingRate = 1.5 // 1.5% per update
                val newBatteryLevel = (batteryLevel + chargingRate).coerceAtMost(100.0)
                onBatteryUpdate(newBatteryLevel)
                
                val totalConsumption = (newBatteryLevel - 20.0) * 0.8 // Assuming 80kWh battery
                
                if (newBatteryLevel >= 80.0 || context.stopRequested) {
                    // Auto-stop at 80% or user request
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STOPPED)
                        consumption(totalConsumption.coerceAtLeast(0.0))
                        duration(context.secondsSinceLastChange * 3)
                    }
                } else {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.CHARGING)
                        consumption(totalConsumption.coerceAtLeast(0.0))
                        duration(context.secondsSinceLastChange * 3)
                    }
                }
            }
            
            else -> context.currentSession
        }
    }
}
```

### Example 3: Network Failure Simulation

```kotlin
class NetworkFailureSimulation {
    
    fun createNetworkFailureStrategy(): SimulatorFlow.Custom {
        var failureCount = 0
        
        return SimulatorFlow.Custom(
            onSessionStart = { println("Starting network failure simulation") },
            onSessionStop = { println("Ending network failure simulation") },
            onSessionStatusUpdate = { context ->
                simulateNetworkIssues(context, failureCount) { count ->
                    failureCount = count
                }
            }
        )
    }
    
    private fun simulateNetworkIssues(
        context: SimulationContext,
        failureCount: Int,
        onFailureUpdate: (Int) -> Unit
    ): ChargingSession? {
        val sessionFactory = DefaultChargingSessionFactory()
        
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
                // Simulate intermittent start failures
                if (failureCount < 2 && Math.random() < 0.3) {
                    onFailureUpdate(failureCount + 1)
                    null // Simulate network timeout
                } else {
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STARTED)
                        consumption(0.0)
                        duration(0)
                    }
                }
            }
            
            SessionStatus.STARTED -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.CHARGING)
                    consumption(1.0)
                    duration(context.secondsSinceLastChange)
                }
            }
            
            SessionStatus.CHARGING -> {
                // Simulate random interruptions
                if (Math.random() < 0.1) { // 10% chance of interruption
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.STOP_REQUESTED) // Forced stop due to network
                        consumption(context.currentSession?.consumption ?: 0.0)
                        duration(context.currentSession?.duration ?: 0)
                    }
                } else {
                    val newConsumption = (context.currentSession?.consumption ?: 0.0) + 0.2
                    sessionFactory.createSession {
                        evseId(context.evseId)
                        status(SessionStatus.CHARGING)
                        consumption(newConsumption)
                        duration(context.secondsSinceLastChange)
                    }
                }
            }
            
            SessionStatus.STOP_REQUESTED -> {
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.STOPPED)
                    consumption(context.currentSession?.consumption ?: 0.0)
                    duration(context.currentSession?.duration ?: 0)
                }
            }
            
            else -> context.currentSession
        }
    }
}
```

## Testing Patterns

### Pattern 1: State Machine Testing

```kotlin
class StateMachineTest {
    
    @Test
    fun `test all state transitions`() = runTest {
        val transitions = mapOf(
            SessionStatus.START_REQUESTED to SessionStatus.STARTED,
            SessionStatus.STARTED to SessionStatus.CHARGING,
            SessionStatus.CHARGING to SessionStatus.STOP_REQUESTED,
            SessionStatus.STOP_REQUESTED to SessionStatus.STOPPED
        )
        
        val repository = RefactoredFakeChargingRepository(mockChargingStore)
        
        var currentStatus: SessionStatus? = null
        val job = launch {
            repository.activeSessions
                .filterNotNull()
                .collect { session ->
                    val previousStatus = currentStatus
                    currentStatus = session.status1
                    
                    // Verify expected transition
                    if (previousStatus != null) {
                        assertThat(transitions[previousStatus]).isEqualTo(currentStatus)
                    }
                }
        }
        
        // Execute full flow
        repository.fetchChargingSession() // START_REQUESTED
        repository.fetchChargingSession() // STARTED
        repository.fetchChargingSession() // CHARGING
        repository.stopChargingSession()
        repository.fetchChargingSession() // STOPPED
        
        job.cancel()
    }
}
```

### Pattern 2: Performance Testing

```kotlin
class PerformanceTest {
    
    @Test
    fun `test simulation performance under load`() = runTest {
        val repository = RefactoredFakeChargingRepository(mockChargingStore)
        val sessionUpdates = mutableListOf<ChargingSession>()
        
        val startTime = System.currentTimeMillis()
        
        val job = launch {
            repository.activeSessions
                .filterNotNull()
                .collect { session ->
                    sessionUpdates.add(session)
                }
        }
        
        // Generate 1000 session updates
        repeat(1000) {
            repository.fetchChargingSession()
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        job.cancel()
        
        // Verify performance
        assertThat(duration).isLessThan(5000) // Should complete in under 5 seconds
        assertThat(sessionUpdates.size).isEqualTo(1000)
        
        println("Generated ${sessionUpdates.size} updates in ${duration}ms")
        println("Average: ${duration.toDouble() / sessionUpdates.size}ms per update")
    }
}
```

## Real-World Scenarios

### Scenario 1: Fleet Management Testing

```kotlin
class FleetManagementTest {
    
    @Test
    fun `test multiple vehicles charging simultaneously`() = runTest {
        val vehicles = listOf("Vehicle1", "Vehicle2", "Vehicle3")
        val repositories = vehicles.map { vehicleId ->
            vehicleId to RefactoredFakeChargingRepository(mockChargingStore)
        }.toMap()
        
        val allSessions = mutableMapOf<String, MutableList<ChargingSession>>()
        
        // Start monitoring all vehicles
        val jobs = repositories.map { (vehicleId, repo) ->
            allSessions[vehicleId] = mutableListOf()
            
            launch {
                repo.activeSessions
                    .filterNotNull()
                    .collect { session ->
                        allSessions[vehicleId]?.add(session)
                    }
            }
        }
        
        // Start charging all vehicles
        repositories.values.forEach { repo ->
            repo.fetchChargingSession() // Start each vehicle
        }
        
        // Simulate charging for different durations
        repeat(10) {
            repositories.values.forEach { repo ->
                repo.fetchChargingSession()
            }
            delay(100)
        }
        
        // Stop all vehicles
        repositories.values.forEach { repo ->
            repo.stopChargingSession()
            repo.fetchChargingSession()
        }
        
        jobs.forEach { it.cancel() }
        
        // Verify all vehicles completed charging
        allSessions.values.forEach { sessions ->
            assertThat(sessions.last().status1).isEqualTo(SessionStatus.STOPPED)
        }
    }
}
```

### Scenario 2: Payment Integration Testing

```kotlin
class PaymentIntegrationTest {
    
    @Test
    fun `test charging with payment failures`() = runTest {
        val customFlow = SimulatorFlow.Custom(
            onSessionStart = { },
            onSessionStop = { },
            onSessionStatusUpdate = { context ->
                simulatePaymentIssues(context)
            }
        )
        
        val config = ChargeConfig(
            environment = Environment.Simulator(simulatorFlow = customFlow)
        )
        
        val repository = RefactoredFakeChargingRepository(mockChargingStore)
        
        // Start charging
        repository.fetchChargingSession()
        repository.fetchChargingSession() // Move to charging
        
        // Simulate payment failure during stop
        repository.stopChargingSession()
        val result = repository.fetchChargingSession()
        
        // Should handle payment failure gracefully
        assertThat(result.isLeft()).isTrue()
    }
    
    private fun simulatePaymentIssues(context: SimulationContext): ChargingSession? {
        val sessionFactory = DefaultChargingSessionFactory()
        
        return when (context.currentStatus) {
            SessionStatus.STOP_REQUESTED -> {
                // Simulate payment processing failure
                sessionFactory.createSession {
                    evseId(context.evseId)
                    status(SessionStatus.STOP_REJECTED)
                    consumption(context.currentSession?.consumption ?: 0.0)
                    duration(context.currentSession?.duration ?: 0)
                }
            }
            else -> {
                // Use default behavior for other states
                DefaultSimulationStrategy(sessionFactory).generateNextSession(context)
            }
        }
    }
}
```

---

These examples demonstrate the flexibility and power of the simulator framework. You can combine these patterns to create comprehensive test suites that cover all aspects of your charging application.