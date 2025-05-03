package de.elvah.charge.features.payments.data.remote.model.response

class GetPublishableKeyResponse(
    val data: Data,
) {
    class Data(
        val publishableKey: String,
    )
}