package de.elvah.charge.features.adhoc_charging.domain.repository

internal interface ChargingStore {

    fun getToken(): String

    fun setToken(token: String)
}
