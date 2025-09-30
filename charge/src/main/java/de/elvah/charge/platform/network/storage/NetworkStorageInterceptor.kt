package de.elvah.charge.platform.network.storage

import okhttp3.Interceptor

public interface NetworkStorageInterceptor : Interceptor {
    public fun clear()
    public fun getStoredRequests(): List<StoredNetworkRequest>
}

public data class StoredNetworkRequest(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val body: String?,
    val timestamp: Long,
    val responseCode: Int?,
    val responseBody: String?
)
