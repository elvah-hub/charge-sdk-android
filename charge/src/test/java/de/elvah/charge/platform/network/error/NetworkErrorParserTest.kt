package de.elvah.charge.platform.network.error

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.net.HttpURLConnection

internal class NetworkErrorParserTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val parser = NetworkErrorParser(moshi)

    @Test
    fun `parseError returns InvalidVersionException for HTTP 400 with api_version_invalid code`() {
        val errorJson = """
        {
          "errors": [
            {
              "status": "400",
              "title": "Bad Request",
              "code": "api.version.invalid",
              "detail": "Invalid API version: 2099-01-01.unknown (valid versions: 2025-04-09.helium, 2025-10-16.lithium)"
            }
          ]
        }
        """.trimIndent()

        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_BAD_REQUEST, errorBody)

        val result = parser.parseError(response)

        assertTrue(
            "Expected InvalidVersionException",
            result is NetworkError.InvalidVersionException
        )
        val exception = result as NetworkError.InvalidVersionException
        assertEquals(
            "Invalid API version: 2099-01-01.unknown (valid versions: 2025-04-09.helium, 2025-10-16.lithium)",
            exception.errorResponse.errors.first().detail
        )
        assertEquals(
            "Invalid API version: Invalid API version: 2099-01-01.unknown (valid versions: 2025-04-09.helium, 2025-10-16.lithium)",
            exception.message
        )
    }

    @Test
    fun `parseError returns VersionTooOldException for HTTP 400 with api_version_too_old code`() {
        val errorJson = """
        {
          "errors": [
            {
              "status": "400",
              "title": "Bad Request",
              "code": "api.version.too_old",
              "detail": "API version 2025-04-09.helium is too old for this endpoint (minimum supported: 2025-10-16.lithium)"
            }
          ]
        }
        """.trimIndent()

        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_BAD_REQUEST, errorBody)

        val result = parser.parseError(response)

        assertTrue("Expected VersionTooOldException", result is NetworkError.VersionTooOldException)
        val exception = result as NetworkError.VersionTooOldException
        assertEquals(
            "API version 2025-04-09.helium is too old for this endpoint (minimum supported: 2025-10-16.lithium)",
            exception.errorResponse.errors.first().detail
        )
        assertEquals(
            "API version too old: API version 2025-04-09.helium is too old for this endpoint (minimum supported: 2025-10-16.lithium)",
            exception.message
        )
    }

    @Test
    fun `parseError returns VersionTooNewException for HTTP 400 with api_version_too_new code`() {
        val errorJson = """
        {
          "errors": [
            {
              "status": "400",
              "title": "Bad Request",
              "code": "api.version.too_new",
              "detail": "API version 2025-10-16.lithium is not supported for this endpoint (removed in: 2025-10-16.lithium)"
            }
          ]
        }
        """.trimIndent()

        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_BAD_REQUEST, errorBody)

        val result = parser.parseError(response)

        assertTrue("Expected VersionTooNewException", result is NetworkError.VersionTooNewException)
        val exception = result as NetworkError.VersionTooNewException
        assertEquals(
            "API version 2025-10-16.lithium is not supported for this endpoint (removed in: 2025-10-16.lithium)",
            exception.errorResponse.errors.first().detail
        )
        assertEquals(
            "API version too new: API version 2025-10-16.lithium is not supported for this endpoint (removed in: 2025-10-16.lithium)",
            exception.message
        )
    }

    @Test
    fun `parseError returns GenericHttpException for HTTP 400 with different error code`() {
        val errorJson = """
        {
          "errors": [
            {
              "status": "400",
              "title": "Bad Request",
              "code": "some.other.error",
              "detail": "Some other error"
            }
          ]
        }
        """.trimIndent()

        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_BAD_REQUEST, errorBody)

        val result = parser.parseError(response)

        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, exception.code)
    }

    @Test
    fun `parseError returns GenericHttpException for HTTP 500`() {
        val errorBody = "Internal Server Error".toResponseBody("text/plain".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_INTERNAL_ERROR, errorBody)

        val result = parser.parseError(response)

        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, exception.code)
        assertEquals("Internal Server Error", exception.errorBody)
    }

    @Test
    fun `parseError returns GenericHttpException for HTTP 400 with invalid JSON`() {
        val errorBody = "invalid json".toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_BAD_REQUEST, errorBody)

        val result = parser.parseError(response)

        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, exception.code)
        assertEquals("invalid json", exception.errorBody)
    }

    @Test
    fun `parseError returns GenericHttpException for HTTP 400 with null error body`() {
        val response = mockk<Response<*>>()
        every { response.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { response.errorBody() } returns null

        val result = parser.parseError(response)

        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, exception.code)
        assertEquals(null, exception.errorBody)
    }
}
