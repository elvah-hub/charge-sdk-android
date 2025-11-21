package de.elvah.charge.features.payments.domain.model

@JvmInline
internal value class PublishableKey(val key: String) {
    init {
        require(key.isNotBlank()) {
            "Publishable key cannot be blank"
        }
    }
}
