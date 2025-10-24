package de.elvah.charge.features.sites.domain.usecase

import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetSitesTest {

    private lateinit var sitesRepository: SitesRepository
    private lateinit var useCase: GetSites

    @Before
    fun setUp() {
        sitesRepository = mockk()
        useCase = GetSites(sitesRepository)
    }

    @Test
    fun `invoke returns success when repository returns success`() = runTest {
        val params = GetSites.Params()
        val sites = listOf(createTestChargeSite("site_1"), createTestChargeSite("site_2"))
        coEvery { sitesRepository.getChargeSites(null, null, null, null) } returns sites.right()

        val result = useCase.invoke(params)

        assertTrue("Expected Right result", result.isRight())
        result.fold(
            ifLeft = { fail("Expected success but got failure: $it") },
            ifRight = { returnedSites ->
                assertEquals(2, returnedSites.size)
                assertEquals("site_1", returnedSites[0].id)
                assertEquals("site_2", returnedSites[1].id)
            }
        )

        coVerify { sitesRepository.getChargeSites(null, null, null, null) }
    }

    @Test
    fun `invoke returns failure when repository returns failure`() = runTest {
        val params = GetSites.Params()
        val exception = RuntimeException("Sites fetch failed")
        coEvery {
            sitesRepository.getChargeSites(
                any(),
                any(),
                any(),
                any()
            )
        } returns exception.left()

        val result = useCase.invoke(params)

        assertTrue("Expected Left result", result.isLeft())
        result.fold(
            ifLeft = { error ->
                assertEquals(exception, error)
            },
            ifRight = { fail("Expected failure but got success: $it") }
        )
    }

    @Test
    fun `invoke passes all parameters correctly`() = runTest {
        val boundingBox = BoundingBox(
            minLat = 52.5,
            maxLat = 52.6,
            minLng = 13.3,
            maxLng = 13.4
        )
        val params = GetSites.Params(
            boundingBox = boundingBox,
            campaignId = "campaign_123",
            organisationId = "org_456",
            offerType = OfferType.CAMPAIGN
        )
        val sites = listOf(createTestChargeSite("site_1"))
        coEvery { sitesRepository.getChargeSites(any(), any(), any(), any()) } returns sites.right()

        val result = useCase.invoke(params)

        assertTrue("Expected Right result", result.isRight())
        coVerify {
            sitesRepository.getChargeSites(
                boundingBox = boundingBox,
                campaignId = "campaign_123",
                organisationId = "org_456",
                offerType = OfferType.CAMPAIGN
            )
        }
    }

    @Test
    fun `invoke handles null parameters correctly`() = runTest {
        val params = GetSites.Params(
            boundingBox = null,
            campaignId = null,
            organisationId = null,
            offerType = null
        )
        val sites = listOf(createTestChargeSite("site_null"))
        coEvery { sitesRepository.getChargeSites(null, null, null, null) } returns sites.right()

        val result = useCase.invoke(params)

        assertTrue("Expected Right result", result.isRight())
        coVerify { sitesRepository.getChargeSites(null, null, null, null) }
    }

    @Test
    fun `Params class handles default values correctly`() {
        val defaultParams = GetSites.Params()

        assertNull("Default boundingBox should be null", defaultParams.boundingBox)
        assertNull("Default campaignId should be null", defaultParams.campaignId)
        assertNull("Default organisationId should be null", defaultParams.organisationId)
        assertNull("Default offerType should be null", defaultParams.offerType)
    }

    @Test
    fun `Params class handles custom values correctly`() {
        val boundingBox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val params = GetSites.Params(
            boundingBox = boundingBox,
            campaignId = "campaign_test",
            organisationId = "org_test",
            offerType = OfferType.CAMPAIGN
        )

        assertEquals("BoundingBox should match", boundingBox, params.boundingBox)
        assertEquals("CampaignId should match", "campaign_test", params.campaignId)
        assertEquals("OrganisationId should match", "org_test", params.organisationId)
        assertEquals("OfferType should match", OfferType.CAMPAIGN, params.offerType)
    }

    private fun createTestChargeSite(id: String) = mockk<ChargeSite> {
        every { this@mockk.id } returns id
    }
}
