package de.elvah.charge.platform.network.error

import retrofit2.Response

/**
 * Sealed class representing different types of network errors that can occur during API calls.
 * This provides type-safe error handling for specific error scenarios.
 */
internal sealed class NetworkError : Throwable() {

    /**
     * Error thrown when the SDK version is no longer supported by the API.
     * This occurs when receiving HTTP 410 (Gone) with specific error payload.
     *
     * @property response The HTTP response that caused this error
     * @property errorResponse The parsed error response containing details
     */
    class OutdatedSdkException(
        val response: Response<*>,
        val errorResponse: ApiErrorResponse,
    ) : NetworkError() {
        override val message: String =
            "SDK version no longer supported: ${errorResponse.errors.firstOrNull()?.title ?: "Unknown error"}"
    }

    /**
     * Generic HTTP error for all other HTTP error responses.
     * This wraps standard HTTP errors that don't match specific error patterns.
     *
     * @property response The HTTP response that caused this error
     * @property code The HTTP status code
     * @property errorBody The raw error response body, if available
     */
    class GenericHttpException(
        val response: Response<*>,
        val code: Int,
        val errorBody: String?,
    ) : NetworkError() {
        override val message: String = "HTTP $code error: ${errorBody ?: "Unknown error"}"
    }
}
