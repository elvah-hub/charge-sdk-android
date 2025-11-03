package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetOrganisationDetailsTest {

    private val getSessionDetails: GetSessionDetails = mockk()
    private val getOrganisationDetails = GetOrganisationDetails(getSessionDetails)

    @Test
    fun `invoke returns correct OrganisationDetails`() = runTest {
        // Given
        val sessionDetails = ChargingSessionPrefs.newBuilder()
            .setPrivacyUrl("https://elvah.de/privacy")
            .setTermsOfConditionUrl("https://elvah.de/toc")
            .setCpoName("Elvah")
            .setLogoUrl("https://elvah.de/logo.png")
            .setEmail("support@elvah.de")
            .setWhatsapp("+49123456789")
            .setPhone("+49987654321")
            .setAgent("Elvah Support")
            .build()

        coEvery { getSessionDetails() } returns sessionDetails

        // When
        val result = getOrganisationDetails()

        // Then
        val expected = OrganisationDetails(
            privacyUrl = "https://elvah.de/privacy",
            termsOfConditionUrl = "https://elvah.de/toc",
            companyName = "Elvah",
            logoUrl = "https://elvah.de/logo.png",
            supportContacts = SupportContacts(
                email = "support@elvah.de",
                whatsapp = "+49123456789",
                phone = "+49987654321",
                agent = "Elvah Support"
            )
        )
        assertEquals(expected, result)
    }
}
