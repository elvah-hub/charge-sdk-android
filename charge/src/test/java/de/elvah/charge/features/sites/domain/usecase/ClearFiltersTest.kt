package de.elvah.charge.features.sites.domain.usecase

import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ClearFiltersTest {

    private val filtersRepository: FiltersRepository = mockk(relaxed = true)
    private val clearFilters = ClearFilters(filtersRepository)

    @Test
    fun `invoke calls clearFilters on repository`() = runBlocking {
        // When
        clearFilters()

        // Then
        coVerify { filtersRepository.clearFilters() }
    }
}
