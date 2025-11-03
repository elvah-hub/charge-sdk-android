package de.elvah.charge.platform.network

import de.elvah.charge.platform.network.discovery.NetworkStorageInterceptorRegistry
import de.elvah.charge.platform.network.storage.NetworkStorageInterceptor
import de.elvah.charge.platform.network.storage.StoredNetworkRequest

public object NetworkRequestStorageManager {

    public fun registerStorageInterceptor(interceptor: NetworkStorageInterceptor) {
        NetworkStorageInterceptorRegistry.register(interceptor)
    }

    public fun getAllStoredRequests(): List<StoredNetworkRequest> {
        return NetworkStorageInterceptorRegistry.getStorageInterceptors()
            .flatMap { it.getStoredRequests() }
            .sortedBy { it.timestamp }
    }

    public fun clearAllStoredRequests() {
        NetworkStorageInterceptorRegistry.clearAllStoredRequests()
    }

    public fun getStorageInterceptorsCount(): Int {
        return NetworkStorageInterceptorRegistry.getStorageInterceptors().size
    }
}
