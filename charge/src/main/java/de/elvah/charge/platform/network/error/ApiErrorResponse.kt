package de.elvah.charge.platform.network.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data class representing the standard API error response format.
 * This matches the JSON structure returned by the API for error cases.
 */
@JsonClass(generateAdapter = true)
internal data class ApiErrorResponse(
    @param:Json(name = "errors") val errors: List<ApiError>,
)

/**
 * Data class representing an individual error within the API error response.
 */
@JsonClass(generateAdapter = true)
internal data class ApiError(
    @param:Json(name = "status") val status: String,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "code") val code: String,
)
