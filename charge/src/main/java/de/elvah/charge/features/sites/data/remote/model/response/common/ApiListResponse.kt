package de.elvah.charge.features.sites.data.remote.model.response.common

import com.squareup.moshi.Json

data class ApiListResponse<T>(
    @param:Json(name = "data")
    val data: List<T>,
)

