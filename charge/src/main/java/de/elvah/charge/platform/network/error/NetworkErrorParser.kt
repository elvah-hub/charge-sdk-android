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
            HttpURLConnection.HTTP_BAD_REQUEST -> parseBadRequestError(response, errorBody)
            else -> NetworkError.GenericHttpException(
                response = response,
                code = response.code(),
                errorBody = errorBody
            )
        }
    }

    private fun parseBadRequestError(response: Response<*>, errorBody: String?): NetworkError {
        if (errorBody.isNullOrBlank()) {
            return NetworkError.GenericHttpException(
                response = response,
                code = response.code(),
                errorBody = errorBody
            )
        }

        return try {
            val apiErrorResponse = apiErrorAdapter.fromJson(errorBody)
            if (apiErrorResponse != null) {
                when (getApiVersionErrorType(apiErrorResponse)) {
                    ApiVersionErrorType.INVALID -> NetworkError.InvalidVersionException(
                        response = response,
                        errorResponse = apiErrorResponse
                    )
                    ApiVersionErrorType.TOO_OLD -> NetworkError.VersionTooOldException(
                        response = response,
                        errorResponse = apiErrorResponse
                    )
                    ApiVersionErrorType.TOO_NEW -> NetworkError.VersionTooNewException(
                        response = response,
                        errorResponse = apiErrorResponse
                    )
                    ApiVersionErrorType.NONE -> NetworkError.GenericHttpException(
                        response = response,
                        code = response.code(),
                        errorBody = errorBody
                    )
                }
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

    private fun getApiVersionErrorType(apiErrorResponse: ApiErrorResponse): ApiVersionErrorType {
        return apiErrorResponse.errors.firstOrNull()?.let { error ->
            when (error.code) {
                "api.version.invalid" -> ApiVersionErrorType.INVALID
                "api.version.too_old" -> ApiVersionErrorType.TOO_OLD
                "api.version.too_new" -> ApiVersionErrorType.TOO_NEW
                else -> ApiVersionErrorType.NONE
            }
        } ?: ApiVersionErrorType.NONE
    }

    private enum class ApiVersionErrorType {
        INVALID, TOO_OLD, TOO_NEW, NONE
    }
}
