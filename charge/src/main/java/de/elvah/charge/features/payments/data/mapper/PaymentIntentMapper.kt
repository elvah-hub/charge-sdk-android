package de.elvah.charge.features.payments.data.mapper

import de.elvah.charge.features.payments.data.remote.model.response.CreatePaymentIntentResponse
import de.elvah.charge.features.payments.domain.model.PaymentIntent


internal fun CreatePaymentIntentResponse.toDomain(): PaymentIntent {
    return PaymentIntent(
        paymentIntentId = data.paymentIntentId,
        accountId = data.accountId,
        clientSecret = data.clientSecret,
        amount = data.authorisationAmount.value,
        currency = data.authorisationAmount.currency,
        paymentId = data.paymentId
    )
}