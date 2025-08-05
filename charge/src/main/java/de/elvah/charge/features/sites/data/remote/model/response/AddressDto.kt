package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

data class AddressDto(

    @param:Json(name = "streetAddress")
    val streetAddress: List<String>,

    @param:Json(name = "postalCode")
    val postalCode: String?,

    @param:Json(name = "locality")
    val locality: String?
)
