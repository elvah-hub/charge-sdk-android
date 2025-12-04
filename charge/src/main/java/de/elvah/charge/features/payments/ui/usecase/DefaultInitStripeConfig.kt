package de.elvah.charge.features.payments.ui.usecase

import android.content.Context
import com.stripe.android.PaymentConfiguration
import de.elvah.charge.features.payments.domain.model.PublishableKey


internal class DefaultInitStripeConfig(private val context: Context) : InitStripeConfig {

    override operator fun invoke(publishableKey: PublishableKey, accountId: String?) {
        PaymentConfiguration.init(context = context, publishableKey = publishableKey.key, accountId)
    }
}
