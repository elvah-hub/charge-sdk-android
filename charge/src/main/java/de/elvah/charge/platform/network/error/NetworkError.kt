package de.elvah.charge.platform.network.error

import retrofit2.Response

/**
 * Sealed class representing different types of network errors that can occur during API calls.
 * This provides type-safe error handling for specific error scenarios.
 */
internal sealed class NetworkError : Throwable() {

    /**
     * Error thrown when the API version is invalid.
     * This occurs when receiving HTTP 400 with error code "api.version.invalid".
     *
     * @property response The HTTP response that caused this error
     * @property errorResponse The parsed error response containing details
     */
    class InvalidVersionException(
        val response: Response<*>,
        val errorResponse: ApiErrorResponse,
    ) : NetworkError() {
        override val message: String =
            "Invalid API version: ${errorResponse.errors.firstOrNull()?.detail ?: "Unknown error"}"
    }

    /**
     * Error thrown when the API version is too old.
     * This occurs when receiving HTTP 400 with error code "api.version.too_old".
     *
     * @property response The HTTP response that caused this error
     * @property errorResponse The parsed error response containing details
     */
    class VersionTooOldException(
        val response: Response<*>,
        val errorResponse: ApiErrorResponse,
    ) : NetworkError() {
        override val message: String =
            "API version too old: ${errorResponse.errors.firstOrNull()?.detail ?: "Unknown error"}"
    }

    /**
     * Error thrown when the API version is too new or removed.
     * This occurs when receiving HTTP 400 with error code "api.version.too_new".
     *
     * @property response The HTTP response that caused this error
     * @property errorResponse The parsed error response containing details
     */
    class VersionTooNewException(
        val response: Response<*>,
        val errorResponse: ApiErrorResponse,
    ) : NetworkError() {
        override val message: String =
            "API version too new: ${errorResponse.errors.firstOrNull()?.detail ?: "Unknown error"}"
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
