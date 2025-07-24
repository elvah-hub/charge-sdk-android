package de.elvah.charge.features.payments.ui.usecase

import android.content.Context
import com.stripe.android.PaymentConfiguration


internal class DefaultInitStripeConfig(private val context: Context) : InitStripeConfig {

    override operator fun invoke(publishableKey: String, accountId: String) {
        PaymentConfiguration.init(context = context, publishableKey = publishableKey, accountId)
    }
}
