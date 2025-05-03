package de.elvah.charge.features.adhoc_charging.ui

object SupportConfiguration {

    fun setSupportOptions(
        phoneNumber: String? = null,
        email: String? = null,
        whatsapp: String? = null,
        agentSupport: Boolean? = null,
    ) {

    }
}


internal data class SupportOptions(
    val phoneNumber: String? = null,
    val email: String? = null,
    val whatsapp: String? = null,
    val agentSupport: Boolean? = null,
)