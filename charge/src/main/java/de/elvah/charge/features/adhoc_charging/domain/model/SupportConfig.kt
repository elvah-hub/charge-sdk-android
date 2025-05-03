package de.elvah.charge.features.adhoc_charging.domain.model

internal data class SupportConfig(
    val phoneNumber: String? = null,
    val email: String? = null,
    val whatsapp: String? = null,
    val agentSupport: Boolean? = null,
)