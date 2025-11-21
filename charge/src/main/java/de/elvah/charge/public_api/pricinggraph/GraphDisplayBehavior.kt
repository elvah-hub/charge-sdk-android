package de.elvah.charge.public_api.pricinggraph

/**
 * Defines when the pricing graph should display the energy price line chart.
 */
public enum class GraphDisplayBehavior {
    /**
     * Always show the energy price line chart regardless of dynamic pricing availability.
     * This is the default behavior to maintain backward compatibility.
     */
    ALWAYS,
    
    /**
     * Only show the energy price line chart when dynamic pricing is available for the site.
     * If dynamic pricing is not available, the chart will be hidden.
     */
    WHEN_DYNAMIC_PRICING_AVAILABLE
}