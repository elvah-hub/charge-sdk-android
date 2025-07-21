package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

data class SitesDto<T>(

    @param:Json(name = "address")
    val address: AddressDto,

    @param:Json(name = "evses")
    val evses: List<ChargePointDto<T>>,

    @param:Json(name = "location")
    val location: List<Double>,

    @param:Json(name = "id")
    val id: String,

    @param:Json(name = "operatorName")
    val operatorName: String,

    @param:Json(name = "prevalentPowerType")
    val prevalentPowerType: String
)

