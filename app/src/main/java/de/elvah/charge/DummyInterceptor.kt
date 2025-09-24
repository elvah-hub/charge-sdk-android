package de.elvah.charge

import android.util.Log
import de.elvah.charge.platform.network.annotations.NetworkRequestStorage
import de.elvah.charge.platform.network.storage.NetworkStorageInterceptor
import de.elvah.charge.platform.network.storage.StoredNetworkRequest
import okhttp3.Interceptor
import okhttp3.Response

@NetworkRequestStorage
class DummyInterceptor : NetworkStorageInterceptor {
    
    private val storedRequests = mutableListOf<StoredNetworkRequest>()
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("DummyInterceptor", "Request: ${request.url}")
        
        val response = chain.proceed(request)
        
        // Store the request for testing purposes
        val storedRequest = StoredNetworkRequest(
            url = request.url.toString(),
            method = request.method,
            headers = request.headers.toMap(),
            body = null, // Could extract body if needed
            timestamp = System.currentTimeMillis(),
            responseCode = response.code,
            responseBody = null // Could extract response body if needed
        )
        
        storedRequests.add(storedRequest)
        Log.d("DummyInterceptor", "Stored request. Total: ${storedRequests.size}")
        
        return response
    }
    
    override fun clear() {
        storedRequests.clear()
        Log.d("DummyInterceptor", "Cleared stored requests")
    }
    
    override fun getStoredRequests(): List<StoredNetworkRequest> {
        return storedRequests.toList()
    }
}
