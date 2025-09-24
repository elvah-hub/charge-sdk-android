package de.elvah.charge.platform.network.storage

import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryNetworkStorageInterceptorTest {

    private lateinit var interceptor: InMemoryNetworkStorageInterceptor
    private lateinit var mockChain: Interceptor.Chain

    @Before
    fun setUp() {
        interceptor = InMemoryNetworkStorageInterceptor()
        mockChain = mockk()
    }

    @Test
    fun `intercept stores GET request with response`() {
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .get()
            .addHeader("Authorization", "Bearer token")
            .build()
            
        val responseBody = "{\"result\": \"success\"}".toResponseBody("application/json".toMediaType())
        val response = Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(responseBody)
            .build()

        every { mockChain.request() } returns request
        every { mockChain.proceed(request) } returns response

        interceptor.intercept(mockChain)

        val storedRequests = interceptor.getStoredRequests()
        assertEquals(1, storedRequests.size)

        val storedRequest = storedRequests.first()
        assertEquals("https://api.example.com/test", storedRequest.url)
        assertEquals("GET", storedRequest.method)
        assertEquals(200, storedRequest.responseCode)
        assertEquals("{\"result\": \"success\"}", storedRequest.responseBody)
        assertTrue(storedRequest.headers.containsKey("Authorization"))
        assertEquals("Bearer token", storedRequest.headers["Authorization"])
    }

    @Test
    fun `intercept stores POST request with body`() {
        val requestBody = "{\"data\": \"test\"}".toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.example.com/submit")
            .post(requestBody)
            .build()
            
        val response = Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(201)
            .message("Created")
            .build()

        every { mockChain.request() } returns request
        every { mockChain.proceed(request) } returns response

        interceptor.intercept(mockChain)

        val storedRequests = interceptor.getStoredRequests()
        assertEquals(1, storedRequests.size)

        val storedRequest = storedRequests.first()
        assertEquals("https://api.example.com/submit", storedRequest.url)
        assertEquals("POST", storedRequest.method)
        assertEquals("{\"data\": \"test\"}", storedRequest.body)
        assertEquals(201, storedRequest.responseCode)
    }

    @Test
    fun `intercept handles multiple requests`() {
        val request1 = Request.Builder().url("https://api.example.com/test1").get().build()
        val request2 = Request.Builder().url("https://api.example.com/test2").get().build()
        
        val response1 = Response.Builder()
            .request(request1)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
            
        val response2 = Response.Builder()
            .request(request2)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(404)
            .message("Not Found")
            .build()

        every { mockChain.request() } returns request1 andThen request2
        every { mockChain.proceed(request1) } returns response1
        every { mockChain.proceed(request2) } returns response2

        interceptor.intercept(mockChain)
        interceptor.intercept(mockChain)

        val storedRequests = interceptor.getStoredRequests()
        assertEquals(2, storedRequests.size)
        
        assertEquals("https://api.example.com/test1", storedRequests[0].url)
        assertEquals(200, storedRequests[0].responseCode)
        
        assertEquals("https://api.example.com/test2", storedRequests[1].url)
        assertEquals(404, storedRequests[1].responseCode)
    }

    @Test
    fun `clear removes all stored requests`() {
        val request = Request.Builder().url("https://api.example.com/test").get().build()
        val response = Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        every { mockChain.request() } returns request
        every { mockChain.proceed(request) } returns response

        interceptor.intercept(mockChain)
        assertEquals(1, interceptor.getStoredRequests().size)

        interceptor.clear()
        assertTrue(interceptor.getStoredRequests().isEmpty())
    }

    @Test
    fun `stored requests are thread-safe`() {
        val numRequests = 100
        val threads = mutableListOf<Thread>()
        
        repeat(numRequests) { i ->
            val thread = Thread {
                val request = Request.Builder().url("https://api.example.com/test$i").get().build()
                val response = Response.Builder()
                    .request(request)
                    .protocol(okhttp3.Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .build()
                
                val chainMock = mockk<Interceptor.Chain>()
                every { chainMock.request() } returns request
                every { chainMock.proceed(request) } returns response
                
                interceptor.intercept(chainMock)
            }
            threads.add(thread)
            thread.start()
        }
        
        threads.forEach { it.join() }
        assertEquals(numRequests, interceptor.getStoredRequests().size)
    }
}