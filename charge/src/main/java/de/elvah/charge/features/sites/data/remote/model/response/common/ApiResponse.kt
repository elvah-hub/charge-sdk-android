package de.elvah.charge.features.sites.data.remote.model.response.common

import com.squareup.moshi.Json

data class ApiResponse<T>(
    @param:Json(name = "data")
    val data: T,
)
