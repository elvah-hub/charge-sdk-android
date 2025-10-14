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
    fun `parseError returns OutdatedSdkException for HTTP 410 with correct payload`() {
        val errorJson = """
        {
          "errors": [
            {
              "status": "GONE",
              "title": "API Version no longer supported",
              "code": "410"
            }
          ]
        }
        """.trimIndent()
        
        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_GONE, errorBody)
        
        val result = parser.parseError(response)
        
        assertTrue("Expected OutdatedSdkException", result is NetworkError.OutdatedSdkException)
        val exception = result as NetworkError.OutdatedSdkException
        assertEquals("API Version no longer supported", exception.errorResponse.errors.first().title)
        assertEquals("SDK version no longer supported: API Version no longer supported", exception.message)
    }

    @Test
    fun `parseError returns GenericHttpException for HTTP 410 with incorrect payload`() {
        val errorJson = """
        {
          "errors": [
            {
              "status": "GONE",
              "title": "Some other error",
              "code": "410"
            }
          ]
        }
        """.trimIndent()
        
        val errorBody = errorJson.toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_GONE, errorBody)
        
        val result = parser.parseError(response)
        
        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_GONE, exception.code)
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
    fun `parseError returns GenericHttpException for HTTP 410 with invalid JSON`() {
        val errorBody = "invalid json".toResponseBody("application/json".toMediaType())
        val response = Response.error<Any>(HttpURLConnection.HTTP_GONE, errorBody)
        
        val result = parser.parseError(response)
        
        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_GONE, exception.code)
        assertEquals("invalid json", exception.errorBody)
    }

    @Test
    fun `parseError returns GenericHttpException for HTTP 410 with null error body`() {
        val response = mockk<Response<*>>()
        every { response.code() } returns HttpURLConnection.HTTP_GONE
        every { response.errorBody() } returns null
        
        val result = parser.parseError(response)
        
        assertTrue("Expected GenericHttpException", result is NetworkError.GenericHttpException)
        val exception = result as NetworkError.GenericHttpException
        assertEquals(HttpURLConnection.HTTP_GONE, exception.code)
        assertEquals(null, exception.errorBody)
    }
}