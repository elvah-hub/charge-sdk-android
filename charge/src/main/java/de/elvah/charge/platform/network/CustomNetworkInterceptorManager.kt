package de.elvah.charge.platform.network

import de.elvah.charge.platform.network.discovery.NetworkInterceptorRegistry
import okhttp3.Interceptor

public object CustomNetworkInterceptorManager {
    
    public fun registerInterceptor(interceptor: Interceptor) {
        NetworkInterceptorRegistry.register(interceptor)
    }
    
    public fun getCustomInterceptorsCount(): Int {
        return NetworkInterceptorRegistry.getCustomInterceptors().size
    }
    
    public fun clearAllInterceptors() {
        NetworkInterceptorRegistry.clear()
    }
}
