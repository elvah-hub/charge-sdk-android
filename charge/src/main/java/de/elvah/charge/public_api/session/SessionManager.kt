package de.elvah.charge.public_api.session

import android.content.Context
import de.elvah.charge.features.sites.ui.utils.goToChargingSession
import de.elvah.charge.public_api.session.usecase.GetActiveChargingSession

public object SessionManager {

    private val getActiveChargingSession = GetActiveChargingSession()

    public fun openSession(context: Context) {
        context.goToChargingSession()
    }

    public suspend fun isSessionActive(): Boolean {
        return getActiveChargingSession().isRight()
    }
}
