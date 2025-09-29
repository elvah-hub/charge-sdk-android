package de.elvah.charge.features.payments.data.remote.model.response

internal class GetPublishableKeyResponse(
    val data: Data,
) {
    class Data(
        val publishableKey: String,
    )
}
