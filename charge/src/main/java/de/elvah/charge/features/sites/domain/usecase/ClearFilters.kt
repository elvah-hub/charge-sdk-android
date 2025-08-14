package de.elvah.charge.features.sites.domain.usecase

import de.elvah.charge.features.sites.domain.repository.FiltersRepository


internal class ClearFilters(
    private val filtersRepository: FiltersRepository,
) {
    suspend operator fun invoke() {
        filtersRepository.clearFilters()
    }
}
