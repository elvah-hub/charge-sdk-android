package de.elvah.charge.features.payments.domain.mapper

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SessionDetailsMapperTest {

    @Test
    fun `getOrganisationDetails maps all required fields correctly`() {
        val prefs = createChargingSessionPrefs(
            cpoName = "Test CPO Company",
            privacyUrl = "https://example.com/privacy-policy",
            termsOfConditionUrl = "https://example.com/terms-conditions",
            logoUrl = "https://example.com/logo.png"
        )
        
        val organisationDetails = prefs.getOrganisationDetails()
        
        assertEquals("Test CPO Company", organisationDetails.companyName)
        assertEquals("https://example.com/privacy-policy", organisationDetails.privacyUrl)
        assertEquals("https://example.com/terms-conditions", organisationDetails.termsOfConditionUrl)
        assertEquals("https://example.com/logo.png", organisationDetails.logoUrl)
    }

    @Test
    fun `getOrganisationDetails maps all support contact fields correctly`() {
        val prefs = createChargingSessionPrefs(
            email = "support@example.com",
            whatsapp = "+49123456789",
            phone = "+49987654321",
            agent = "John Support Agent"
        )
        
        val organisationDetails = prefs.getOrganisationDetails()
        val supportContacts = organisationDetails.supportContacts
        
        assertEquals("support@example.com", supportContacts.email)
        assertEquals("+49123456789", supportContacts.whatsapp)
        assertEquals("+49987654321", supportContacts.phone)
        assertEquals("John Support Agent", supportContacts.agent)
    }

    @Test
    fun `getOrganisationDetails handles null support contact fields`() {
        val prefs = createChargingSessionPrefs(
            email = null,
            whatsapp = null,
            phone = null,
            agent = null
        )
        
        val organisationDetails = prefs.getOrganisationDetails()
        val supportContacts = organisationDetails.supportContacts
        
        assertEquals("",supportContacts.email)
        assertEquals("",supportContacts.whatsapp)
        assertEquals("",supportContacts.phone)
        assertEquals("",supportContacts.agent)
    }

    @Test
    fun `getOrganisationDetails handles empty string support contact fields`() {
        val prefs = createChargingSessionPrefs(
            email = "",
            whatsapp = "",
            phone = "",
            agent = ""
        )
        
        val organisationDetails = prefs.getOrganisationDetails()
        val supportContacts = organisationDetails.supportContacts
        
        assertEquals("", supportContacts.email)
        assertEquals("", supportContacts.whatsapp)
        assertEquals("", supportContacts.phone)
        assertEquals("", supportContacts.agent)
    }

    @Test
    fun `getOrganisationDetails handles partial support contact information`() {
        val prefs = createChargingSessionPrefs(
            email = "support@example.com",
            whatsapp = null,
            phone = "+49123456789",
            agent = null
        )
        
        val organisationDetails = prefs.getOrganisationDetails()
        val supportContacts = organisationDetails.supportContacts
        
        assertEquals("support@example.com", supportContacts.email)
        assertEquals("",supportContacts.whatsapp)
        assertEquals("+49123456789", supportContacts.phone)
        assertEquals("", supportContacts.agent)
    }

    @Test
    fun `getOrganisationDetails creates nested SupportContacts object correctly`() {
        val prefs = createChargingSessionPrefs(
            email = "test@example.com",
            whatsapp = "+123456789",
            phone = "+987654321",
            agent = "Test Agent"
        )
        
        val organisationDetails = prefs.getOrganisationDetails()
        val supportContacts = organisationDetails.supportContacts
        
        assertEquals("Nested email mapping failed", "test@example.com", supportContacts.email)
        assertEquals("Nested whatsapp mapping failed", "+123456789", supportContacts.whatsapp)
        assertEquals("Nested phone mapping failed", "+987654321", supportContacts.phone)
        assertEquals("Nested agent mapping failed", "Test Agent", supportContacts.agent)
    }

    @Test
    fun `getOrganisationDetails handles URLs with various formats`() {
        val testCases = listOf(
            "https://example.com/privacy" to "https://example.com/terms",
            "http://test.org/privacy.html" to "http://test.org/terms.html",
            "https://company-name.com/legal/privacy-policy" to "https://company-name.com/legal/terms-of-service",
            "https://example.co.uk/privacy" to "https://example.co.uk/terms"
        )
        
        testCases.forEach { (privacyUrl, termsUrl) ->
            val prefs = createChargingSessionPrefs(
                privacyUrl = privacyUrl,
                termsOfConditionUrl = termsUrl
            )
            
            val organisationDetails = prefs.getOrganisationDetails()
            
            assertEquals("Privacy URL mapping failed for $privacyUrl", privacyUrl, organisationDetails.privacyUrl)
            assertEquals("Terms URL mapping failed for $termsUrl", termsUrl, organisationDetails.termsOfConditionUrl)
        }
    }

    @Test
    fun `getOrganisationDetails handles special characters in company name`() {
        val specialNames = listOf(
            "Müller & Sons GmbH",
            "Café Électrique S.A.",
            "Test-Company (Europe) Ltd.",
            "Charge & Go™"
        )
        
        specialNames.forEach { companyName ->
            val prefs = createChargingSessionPrefs(cpoName = companyName)
            
            val organisationDetails = prefs.getOrganisationDetails()
            
            assertEquals("Company name mapping failed for '$companyName'", companyName, organisationDetails.companyName)
        }
    }

    @Test
    fun `getOrganisationDetails handles international phone numbers`() {
        val phoneNumbers = mapOf(
            "+49123456789" to "+491234567890",
            "+33123456789" to "+441234567890",
            "+1234567890" to "+39123456789"
        )
        
        phoneNumbers.forEach { (whatsapp, phone) ->
            val prefs = createChargingSessionPrefs(
                whatsapp = whatsapp,
                phone = phone
            )
            
            val organisationDetails = prefs.getOrganisationDetails()
            val supportContacts = organisationDetails.supportContacts
            
            assertEquals("WhatsApp number mapping failed", whatsapp, supportContacts.whatsapp)
            assertEquals("Phone number mapping failed", phone, supportContacts.phone)
        }
    }

    private fun createChargingSessionPrefs(
        token: String = "test_token",
        paymentId: String = "test_payment_id",
        cpoName: String = "Test CPO",
        privacyUrl: String = "https://example.com/privacy",
        termsOfConditionUrl: String = "https://example.com/terms",
        logoUrl: String = "https://example.com/logo.png",
        email: String? = "test@example.com",
        whatsapp: String? = "+123456789",
        phone: String? = "+987654321",
        agent: String? = "Test Agent",
        evseId: String = "DE*KDL*E0000040"
    ): ChargingSessionPrefs {
        return ChargingSessionPrefs.newBuilder()
            .setToken(token)
            .setPaymentId(paymentId)
            .setCpoName(cpoName)
            .setPrivacyUrl(privacyUrl)
            .setTermsOfConditionUrl(termsOfConditionUrl)
            .setLogoUrl(logoUrl)
            .apply {
                email?.let { setEmail(it) }
                whatsapp?.let { setWhatsapp(it) }
                phone?.let { setPhone(it) }
                agent?.let { setAgent(it) }
            }
            .setEvseId(evseId)
            .build()
    }
}
