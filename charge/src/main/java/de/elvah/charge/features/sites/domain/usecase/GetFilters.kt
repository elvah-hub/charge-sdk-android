package de.elvah.charge.features.sites.domain.usecase

import de.elvah.charge.features.sites.domain.model.filters.SiteFilter
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import kotlinx.coroutines.flow.Flow


internal class GetFilters(
    private val filtersRepository: FiltersRepository,
) {
    operator fun invoke(): Flow<SiteFilter> {
        return filtersRepository.filters
    }
}
