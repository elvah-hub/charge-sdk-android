package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

data class ApiListResponse<T>(
    @param:Json(name = "data")
    val data: List<T>,
)

data class ApiResponse<T>(
    @param:Json(name = "data")
    val data: T,
)
