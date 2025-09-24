package de.elvah.charge.platform.network.discovery

import de.elvah.charge.platform.network.annotations.NetworkRequestStorage
import de.elvah.charge.platform.network.storage.NetworkStorageInterceptor
import kotlin.reflect.full.findAnnotation

object NetworkStorageInterceptorRegistry {
    
    private val registeredInterceptors = mutableListOf<NetworkStorageInterceptor>()
    
    fun register(interceptor: NetworkStorageInterceptor) {
        registeredInterceptors.add(interceptor)
    }
    
    fun getStorageInterceptors(): List<NetworkStorageInterceptor> {
        return registeredInterceptors.filter { interceptor ->
            interceptor::class.findAnnotation<NetworkRequestStorage>() != null
        }.sortedBy { interceptor ->
            interceptor::class.findAnnotation<NetworkRequestStorage>()?.priority ?: 0
        }
    }
    
    fun clear() {
        registeredInterceptors.clear()
    }
    
    fun clearAllStoredRequests() {
        getStorageInterceptors().forEach { it.clear() }
    }
}