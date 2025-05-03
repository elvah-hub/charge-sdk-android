package de.elvah.charge.platform.bindings.features.deals

import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.features.deals.domain.repository.LocationRepository
import de.elvah.charge.features.deals.domain.usecase.GetDeal
import de.elvah.charge.features.deals.domain.usecase.GetDeals
import de.elvah.charge.features.deals.domain.usecase.GetLocation
import de.elvah.charge.features.deals.domain.usecase.UpdateLocation

internal class UseCaseModule {



    fun providesGetDeals(dealsRepository: DealsRepository): GetDeals = GetDeals(dealsRepository)



    fun providesGetDeal(getDeals: GetDeals): GetDeal = GetDeal(getDeals)



    fun providesGetLocation(locationRepository: LocationRepository): GetLocation =
        GetLocation(locationRepository)



    fun providesUpdateLocation(locationRepository: LocationRepository): UpdateLocation =
        UpdateLocation(locationRepository)
}