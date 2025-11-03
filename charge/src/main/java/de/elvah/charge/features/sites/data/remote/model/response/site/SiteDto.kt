package de.elvah.charge.features.sites.data.remote.model.response.site

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.sites.data.remote.model.response.AddressDto
import de.elvah.charge.features.sites.data.remote.model.response.ChargePointAvailabilityDto
import de.elvah.charge.features.sites.data.remote.model.response.PowerSpecificationDto

@JsonClass(generateAdapter = true)
internal data class SitesDto(

    @param:Json(name = "address")
    val address: AddressDto,

    @param:Json(name = "evses")
    val evses: List<EvseDto>,

    @param:Json(name = "location")
    val location: List<Double>,

    @param:Json(name = "id")
    val id: String,

    @param:Json(name = "operatorName")
    val operatorName: String,

    @param:Json(name = "prevalentPowerType")
    val prevalentPowerType: String
)

@JsonClass(generateAdapter = true)
internal data class EvseDto(

    @param:Json(name = "evseId")
    val evseId: String,

    @param:Json(name = "normalizedEvseId")
    val normalizedEvseId: String,

    @param:Json(name = "powerSpecification")
    val powerSpecification: PowerSpecificationDto?,

    @param:Json(name = "availability")
    val availability: ChargePointAvailabilityDto,

    @param:Json(name = "offer")
    val offer: OfferTypeDto,
)
