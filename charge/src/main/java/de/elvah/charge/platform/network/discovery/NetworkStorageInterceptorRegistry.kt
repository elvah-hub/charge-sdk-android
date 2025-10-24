package de.elvah.charge.platform.network.discovery

import de.elvah.charge.platform.network.annotations.NetworkRequestStorage
import de.elvah.charge.platform.network.storage.NetworkStorageInterceptor
import kotlin.reflect.full.findAnnotation

public object NetworkStorageInterceptorRegistry {

    private val registeredInterceptors = mutableListOf<NetworkStorageInterceptor>()

    public fun register(interceptor: NetworkStorageInterceptor) {
        registeredInterceptors.add(interceptor)
    }

    public fun getStorageInterceptors(): List<NetworkStorageInterceptor> {
        return registeredInterceptors.filter { interceptor ->
            interceptor::class.findAnnotation<NetworkRequestStorage>() != null
        }.sortedBy { interceptor ->
            interceptor::class.findAnnotation<NetworkRequestStorage>()?.priority ?: 0
        }
    }

    public fun clear() {
        registeredInterceptors.clear()
    }

    public fun clearAllStoredRequests() {
        getStorageInterceptors().forEach { it.clear() }
    }
}
