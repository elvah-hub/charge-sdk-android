package de.elvah.charge.features.adhoc_charging.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore




internal class DefaultChargingStore(private val preferences: SharedPreferences) : ChargingStore {

    override fun getToken(): String {
        return preferences.getString("token", "") ?: ""
    }

    override fun setToken(token: String) {
        preferences.edit { putString("token", token) }
    }
}

