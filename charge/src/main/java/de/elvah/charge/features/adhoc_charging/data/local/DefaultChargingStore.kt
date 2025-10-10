package de.elvah.charge.features.adhoc_charging.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import kotlinx.coroutines.flow.Flow


internal class DefaultChargingStore(
    private val context: Context
) : ChargingStore {

    override suspend fun setToken(token: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toBuilder().setToken(token).build()
        }
    }

    override suspend fun setPaymentId(paymentId: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toBuilder().setPaymentId(paymentId).build()
        }
    }

    override suspend fun setEvseId(evseId: String) {
        context.settingsDataStore.updateData { preferences ->
            preferences.toBuilder().setEvseId(evseId).build()
        }
    }

    override suspend fun saveOrganisationDetails(organisationDetails: OrganisationDetails) {
        context.settingsDataStore.updateData { preferences ->
            with(organisationDetails) {
                preferences.toBuilder()
                    .setCpoName(companyName)
                    .setLogoUrl(logoUrl)
                    .setPrivacyUrl(privacyUrl)
                    .setTermsOfConditionUrl(termsOfConditionUrl)
                    .setEmail(supportContacts.email.orEmpty())
                    .setAgent(supportContacts.agent.orEmpty())
                    .setPhone(supportContacts.phone.orEmpty())
                    .setWhatsapp(supportContacts.whatsapp.orEmpty())
                    .build()

            }
        }
    }

    override fun getChargingPrefs(): Flow<ChargingSessionPrefs> {
        return context.settingsDataStore.data
    }

    override suspend fun resetSession() {
        context.settingsDataStore.updateData { preferences ->
            preferences.toBuilder().clear().build()
        }
    }
}

private val Context.settingsDataStore: DataStore<ChargingSessionPrefs> by dataStore(
    fileName = "settings.pb",
    serializer = ChargingSessionPrefsSerializer
)

