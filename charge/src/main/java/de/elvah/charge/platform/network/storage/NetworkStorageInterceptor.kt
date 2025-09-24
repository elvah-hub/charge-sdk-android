package de.elvah.charge.platform.network.storage

import okhttp3.Interceptor

interface NetworkStorageInterceptor : Interceptor {
    fun clear()
    fun getStoredRequests(): List<StoredNetworkRequest>
}

data class StoredNetworkRequest(
    val url: String,
    val method: String,
    val headers: Map<String, String>,
    val body: String?,
    val timestamp: Long,
    val responseCode: Int?,
    val responseBody: String?
)