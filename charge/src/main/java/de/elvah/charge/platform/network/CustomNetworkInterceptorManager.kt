package de.elvah.charge.platform.network

import de.elvah.charge.platform.network.discovery.NetworkInterceptorRegistry
import okhttp3.Interceptor

object CustomNetworkInterceptorManager {
    
    fun registerInterceptor(interceptor: Interceptor) {
        NetworkInterceptorRegistry.register(interceptor)
    }
    
    fun getCustomInterceptorsCount(): Int {
        return NetworkInterceptorRegistry.getCustomInterceptors().size
    }
    
    fun clearAllInterceptors() {
        NetworkInterceptorRegistry.clear()
    }
}