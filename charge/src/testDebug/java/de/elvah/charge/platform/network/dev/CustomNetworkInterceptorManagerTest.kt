package de.elvah.charge.platform.network.dev

import de.elvah.charge.platform.network.annotations.NetworkInterceptor
import de.elvah.charge.platform.network.discovery.NetworkInterceptorRegistry
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CustomNetworkInterceptorManagerTest {

    @Before
    fun setUp() {
        NetworkInterceptorRegistry.clear()
    }

    @After
    fun tearDown() {
        NetworkInterceptorRegistry.clear()
    }

    @Test
    fun `registerInterceptor delegates to registry`() {
        val interceptor = TestCustomInterceptor()
        
        CustomNetworkInterceptorManager.registerInterceptor(interceptor)
        
        assertEquals(1, CustomNetworkInterceptorManager.getCustomInterceptorsCount())
    }

    @Test
    fun `getCustomInterceptorsCount returns correct count`() {
        assertEquals(0, CustomNetworkInterceptorManager.getCustomInterceptorsCount())
        
        CustomNetworkInterceptorManager.registerInterceptor(TestCustomInterceptor())
        assertEquals(1, CustomNetworkInterceptorManager.getCustomInterceptorsCount())
        
        CustomNetworkInterceptorManager.registerInterceptor(TestCustomInterceptor())
        assertEquals(2, CustomNetworkInterceptorManager.getCustomInterceptorsCount())
    }

    @Test
    fun `clearAllInterceptors clears all interceptors`() {
        CustomNetworkInterceptorManager.registerInterceptor(TestCustomInterceptor())
        CustomNetworkInterceptorManager.registerInterceptor(TestCustomInterceptor())
        
        assertEquals(2, CustomNetworkInterceptorManager.getCustomInterceptorsCount())
        
        CustomNetworkInterceptorManager.clearAllInterceptors()
        
        assertEquals(0, CustomNetworkInterceptorManager.getCustomInterceptorsCount())
    }

    @NetworkInterceptor
    class TestCustomInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = mockk()
    }
}