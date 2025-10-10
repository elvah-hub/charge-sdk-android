package de.elvah.charge.public_api

public enum class DisplayBehavior {

    /**
     * Always display the component state.
     */
    WHEN_SOURCE_SET,

    /**
     * Display the component state only after is loaded and available.
     * Loading and error states are hidden by default.
     */
    WHEN_CONTENT_AVAILABLE
}
