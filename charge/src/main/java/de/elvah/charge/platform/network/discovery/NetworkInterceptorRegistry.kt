package de.elvah.charge.platform.network.discovery

import de.elvah.charge.platform.network.annotations.NetworkInterceptor
import okhttp3.Interceptor
import kotlin.reflect.full.findAnnotation

object NetworkInterceptorRegistry {
    
    private val registeredInterceptors = mutableListOf<Interceptor>()
    
    fun register(interceptor: Interceptor) {
        registeredInterceptors.add(interceptor)
    }
    
    fun getCustomInterceptors(): List<Interceptor> {
        return registeredInterceptors.filter { interceptor ->
            interceptor::class.findAnnotation<NetworkInterceptor>() != null
        }.sortedBy { interceptor ->
            interceptor::class.findAnnotation<NetworkInterceptor>()?.priority ?: 0
        }
    }
    
    fun clear() {
        registeredInterceptors.clear()
    }
}