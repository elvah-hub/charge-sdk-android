package de.elvah.charge.features.adhoc_charging.ui.screens.help

import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HelpViewModelTest {

    private val getOrganisationDetails: GetOrganisationDetails = mockk()
    private lateinit var viewModel: HelpViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init success`() = runTest {
        // Given
        val organisationDetails = OrganisationDetails(
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
        coEvery { getOrganisationDetails() } returns organisationDetails

        // When
        viewModel = HelpViewModel(getOrganisationDetails)

        // Then
        assertEquals(HelpState.Loading, viewModel.state.value)
        advanceUntilIdle()
        assertEquals(HelpState.Success(organisationDetails), viewModel.state.value)
    }
}
