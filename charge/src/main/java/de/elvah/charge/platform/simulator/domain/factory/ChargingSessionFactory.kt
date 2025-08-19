package de.elvah.charge.platform.simulator.domain.factory

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus

/**
 * Factory Pattern: Creates ChargingSession instances with validation and defaults.
 * Encapsulates the complex creation logic and provides type safety.
 */
internal interface ChargingSessionFactory {
    
    /**
     * Creates a ChargingSession using a builder pattern.
     */
    fun createSession(builderAction: ChargingSessionBuilder.() -> Unit): ChargingSession
    
    /**
     * Creates a ChargingSession with default values.
     */
    fun createDefaultSession(): ChargingSession
}

/**
 * Default implementation of the ChargingSessionFactory.
 */
internal class DefaultChargingSessionFactory : ChargingSessionFactory {
    
    companion object {
        private const val DEFAULT_EVSE_ID = "DE*KDL*E0000040"
        private const val DEFAULT_STATUS = "auctor"
    }
    
    override fun createSession(builderAction: ChargingSessionBuilder.() -> Unit): ChargingSession {
        val builder = ChargingSessionBuilder()
        builder.builderAction()
        return builder.build()
    }
    
    override fun createDefaultSession(): ChargingSession {
        return ChargingSession(
            evseId = DEFAULT_EVSE_ID,
            status = DEFAULT_STATUS,
            consumption = 0.0,
            duration = 0,
            status1 = SessionStatus.START_REQUESTED
        )
    }
}

/**
 * Builder Pattern: Provides a fluent API for creating ChargingSession objects.
 * Allows optional parameter setting and validation.
 */
internal class ChargingSessionBuilder {
    
    private var evseId: String = "DE*KDL*E0000040"
    private var status: String = "auctor"
    private var consumption: Double = 0.0
    private var duration: Int = 0
    private var status1: SessionStatus = SessionStatus.START_REQUESTED
    
    /**
     * Sets the EVSE ID for the charging session.
     */
    fun evseId(evseId: String): ChargingSessionBuilder = apply {
        require(evseId.isNotBlank()) { "EVSE ID cannot be blank" }
        this.evseId = evseId
    }
    
    /**
     * Sets the status string for the charging session.
     */
    fun status(status: String): ChargingSessionBuilder = apply {
        this.status = status
    }
    
    /**
     * Sets the status enum for the charging session.
     */
    fun status(status1: SessionStatus): ChargingSessionBuilder = apply {
        this.status1 = status1
    }
    
    /**
     * Sets the consumption for the charging session.
     */
    fun consumption(consumption: Double): ChargingSessionBuilder = apply {
        require(consumption >= 0) { "Consumption cannot be negative" }
        this.consumption = consumption
    }
    
    /**
     * Sets the duration for the charging session.
     */
    fun duration(duration: Int): ChargingSessionBuilder = apply {
        require(duration >= 0) { "Duration cannot be negative" }
        this.duration = duration
    }
    
    /**
     * Builds the ChargingSession with the configured parameters.
     */
    fun build(): ChargingSession {
        return ChargingSession(
            evseId = evseId,
            status = status,
            consumption = consumption,
            duration = duration,
            status1 = status1
        )
    }
}