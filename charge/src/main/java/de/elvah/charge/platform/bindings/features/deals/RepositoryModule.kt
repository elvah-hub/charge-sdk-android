package de.elvah.charge.platform.bindings.features.deals

import de.elvah.charge.features.deals.data.DefaultDealsRepository
import de.elvah.charge.features.deals.data.DefaultLocationRepository
import de.elvah.charge.features.deals.data.remote.DealsApi


internal abstract class RepositoryModule {

    companion object {


        fun providesDefaultDealsRepository(dealsApi: DealsApi): DefaultDealsRepository {
            return DefaultDealsRepository(dealsApi)
        }


        fun providesDefaultLocationRepository(): DefaultLocationRepository {
            return DefaultLocationRepository()
        }
    }
}