package de.elvah.charge.platform.network

import de.elvah.charge.platform.network.discovery.NetworkStorageInterceptorRegistry
import de.elvah.charge.platform.network.storage.NetworkStorageInterceptor
import de.elvah.charge.platform.network.storage.StoredNetworkRequest

object NetworkRequestStorageManager {
    
    fun registerStorageInterceptor(interceptor: NetworkStorageInterceptor) {
        NetworkStorageInterceptorRegistry.register(interceptor)
    }
    
    fun getAllStoredRequests(): List<StoredNetworkRequest> {
        return NetworkStorageInterceptorRegistry.getStorageInterceptors()
            .flatMap { it.getStoredRequests() }
            .sortedBy { it.timestamp }
    }
    
    fun clearAllStoredRequests() {
        NetworkStorageInterceptorRegistry.clearAllStoredRequests()
    }
    
    fun getStorageInterceptorsCount(): Int {
        return NetworkStorageInterceptorRegistry.getStorageInterceptors().size
    }
}