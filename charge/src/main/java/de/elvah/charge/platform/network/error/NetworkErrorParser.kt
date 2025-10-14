package de.elvah.charge.platform.network.error

import com.squareup.moshi.Moshi
import retrofit2.Response
import java.net.HttpURLConnection

/**
 * Utility class for parsing HTTP error responses into custom [NetworkError] types.
 * This handles the logic for determining which specific error type to create based on
 * the HTTP response code and body content.
 */
internal class NetworkErrorParser(
    moshi: Moshi,
) {

    private val apiErrorAdapter = moshi.adapter(ApiErrorResponse::class.java)

    /**
     * Parses an HTTP error response and returns the appropriate [NetworkError] subclass.
     *
     * @param response The HTTP error response to parse
     * @return A [NetworkError] representing the specific error type
     */
    fun parseError(response: Response<*>): NetworkError {
        val errorBody = response.errorBody()?.string()

        return when (response.code()) {
            HttpURLConnection.HTTP_GONE -> parseGoneError(response, errorBody)
            else -> NetworkError.GenericHttpException(
                response = response,
                code = response.code(),
                errorBody = errorBody
            )
        }
    }

    private fun parseGoneError(response: Response<*>, errorBody: String?): NetworkError {
        if (errorBody.isNullOrBlank()) {
            return NetworkError.GenericHttpException(
                response = response,
                code = response.code(),
                errorBody = errorBody
            )
        }

        return try {
            val apiErrorResponse = apiErrorAdapter.fromJson(errorBody)
            if (apiErrorResponse != null && isOutdatedSdkError(apiErrorResponse)) {
                NetworkError.OutdatedSdkException(
                    response = response,
                    errorResponse = apiErrorResponse
                )
            } else {
                NetworkError.GenericHttpException(
                    response = response,
                    code = response.code(),
                    errorBody = errorBody
                )
            }
        } catch (e: Exception) {
            // Failed to parse JSON, fallback to generic error
            NetworkError.GenericHttpException(
                response = response,
                code = response.code(),
                errorBody = errorBody
            )
        }
    }

    private fun isOutdatedSdkError(apiErrorResponse: ApiErrorResponse): Boolean {
        return apiErrorResponse.errors.any { error ->
            error.status == "GONE" &&
                    error.code == "410" &&
                    error.title.contains("API Version no longer supported", ignoreCase = true)
        }
    }
}
