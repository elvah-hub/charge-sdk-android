package de.elvah.charge.platform.network.dev

import de.elvah.charge.platform.network.NetworkRequestStorageManager
import de.elvah.charge.platform.network.annotations.NetworkRequestStorage
import de.elvah.charge.platform.network.discovery.NetworkStorageInterceptorRegistry
import de.elvah.charge.platform.network.storage.NetworkStorageInterceptor
import de.elvah.charge.platform.network.storage.StoredNetworkRequest
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkRequestStorageManagerTest {

    @Before
    fun setUp() {
        NetworkStorageInterceptorRegistry.clear()
    }

    @After
    fun tearDown() {
        NetworkStorageInterceptorRegistry.clear()
    }

    @Test
    fun `registerStorageInterceptor delegates to registry`() {
        val interceptor = TestStorageInterceptor()

        NetworkRequestStorageManager.registerStorageInterceptor(interceptor)

        assertEquals(1, NetworkRequestStorageManager.getStorageInterceptorsCount())
    }

    @Test
    fun `getAllStoredRequests returns combined requests from all interceptors sorted by timestamp`() {
        val interceptor1 = TestStorageInterceptor()
        val interceptor2 = TestStorageInterceptor()

        val request1 =
            StoredNetworkRequest("url1", "GET", emptyMap(), null, 1000L, 200, "response1")
        val request2 =
            StoredNetworkRequest("url2", "POST", emptyMap(), "body", 2000L, 201, "response2")
        val request3 = StoredNetworkRequest("url3", "PUT", emptyMap(), null, 1500L, 204, null)

        interceptor1.addRequest(request1)
        interceptor1.addRequest(request3)
        interceptor2.addRequest(request2)

        NetworkRequestStorageManager.registerStorageInterceptor(interceptor1)
        NetworkRequestStorageManager.registerStorageInterceptor(interceptor2)

        val allRequests = NetworkRequestStorageManager.getAllStoredRequests()

        assertEquals(3, allRequests.size)
        assertEquals(request1, allRequests[0]) // timestamp 1000L
        assertEquals(request3, allRequests[1]) // timestamp 1500L
        assertEquals(request2, allRequests[2]) // timestamp 2000L
    }

    @Test
    fun `clearAllStoredRequests clears all interceptors`() {
        val interceptor1 = TestStorageInterceptor()
        val interceptor2 = TestStorageInterceptor()

        interceptor1.addRequest(
            StoredNetworkRequest(
                "url1",
                "GET",
                emptyMap(),
                null,
                1000L,
                200,
                "response1"
            )
        )
        interceptor2.addRequest(
            StoredNetworkRequest(
                "url2",
                "POST",
                emptyMap(),
                "body",
                2000L,
                201,
                "response2"
            )
        )

        NetworkRequestStorageManager.registerStorageInterceptor(interceptor1)
        NetworkRequestStorageManager.registerStorageInterceptor(interceptor2)

        assertEquals(2, NetworkRequestStorageManager.getAllStoredRequests().size)

        NetworkRequestStorageManager.clearAllStoredRequests()

        assertEquals(0, NetworkRequestStorageManager.getAllStoredRequests().size)
    }

    @Test
    fun `getStorageInterceptorsCount returns correct count`() {
        assertEquals(0, NetworkRequestStorageManager.getStorageInterceptorsCount())

        NetworkRequestStorageManager.registerStorageInterceptor(TestStorageInterceptor())
        assertEquals(1, NetworkRequestStorageManager.getStorageInterceptorsCount())

        NetworkRequestStorageManager.registerStorageInterceptor(TestStorageInterceptor())
        assertEquals(2, NetworkRequestStorageManager.getStorageInterceptorsCount())
    }

    @Test
    fun `getAllStoredRequests returns empty list when no interceptors registered`() {
        val allRequests = NetworkRequestStorageManager.getAllStoredRequests()
        assertTrue(allRequests.isEmpty())
    }

    @NetworkRequestStorage
    class TestStorageInterceptor : NetworkStorageInterceptor {
        private val requests = mutableListOf<StoredNetworkRequest>()

        fun addRequest(request: StoredNetworkRequest) {
            requests.add(request)
        }

        override fun intercept(chain: Interceptor.Chain): Response = mockk()

        override fun clear() {
            requests.clear()
        }

        override fun getStoredRequests(): List<StoredNetworkRequest> = requests.toList()
    }
}