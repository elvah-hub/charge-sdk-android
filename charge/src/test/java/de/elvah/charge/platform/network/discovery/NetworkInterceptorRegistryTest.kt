package de.elvah.charge.platform.network.discovery

import de.elvah.charge.platform.network.annotations.NetworkInterceptor
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NetworkInterceptorRegistryTest {

    @Before
    fun setUp() {
        NetworkInterceptorRegistry.clear()
    }

    @After
    fun tearDown() {
        NetworkInterceptorRegistry.clear()
    }

    @Test
    fun `register adds interceptor to registry`() {
        val interceptor = TestCustomInterceptor()

        NetworkInterceptorRegistry.register(interceptor)

        val customInterceptors = NetworkInterceptorRegistry.getCustomInterceptors()
        assertEquals(1, customInterceptors.size)
        assertEquals(interceptor, customInterceptors.first())
    }

    @Test
    fun `getCustomInterceptors returns interceptors sorted by priority`() {
        val lowPriorityInterceptor = LowPriorityCustomInterceptor()
        val highPriorityInterceptor = HighPriorityCustomInterceptor()
        val defaultPriorityInterceptor = TestCustomInterceptor()

        NetworkInterceptorRegistry.register(highPriorityInterceptor)
        NetworkInterceptorRegistry.register(lowPriorityInterceptor)
        NetworkInterceptorRegistry.register(defaultPriorityInterceptor)

        val customInterceptors = NetworkInterceptorRegistry.getCustomInterceptors()
        assertEquals(3, customInterceptors.size)
        assertEquals(defaultPriorityInterceptor, customInterceptors[0]) // priority 0 (default)
        assertEquals(lowPriorityInterceptor, customInterceptors[1]) // priority 1
        assertEquals(highPriorityInterceptor, customInterceptors[2]) // priority 10
    }

    @Test
    fun `clear removes all interceptors from registry`() {
        val interceptor1 = TestCustomInterceptor()
        val interceptor2 = TestCustomInterceptor()

        NetworkInterceptorRegistry.register(interceptor1)
        NetworkInterceptorRegistry.register(interceptor2)

        assertEquals(2, NetworkInterceptorRegistry.getCustomInterceptors().size)

        NetworkInterceptorRegistry.clear()

        assertTrue(NetworkInterceptorRegistry.getCustomInterceptors().isEmpty())
    }

    @NetworkInterceptor
    class TestCustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = mockk()
    }

    @NetworkInterceptor(priority = 1)
    class LowPriorityCustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = mockk()
    }

    @NetworkInterceptor(priority = 10)
    class HighPriorityCustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = mockk()
    }
}