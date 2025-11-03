package de.elvah.charge.platform.network.storage

import de.elvah.charge.platform.network.annotations.NetworkRequestStorage
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.util.concurrent.ConcurrentLinkedQueue

@NetworkRequestStorage(priority = 1)
public class InMemoryNetworkStorageInterceptor : NetworkStorageInterceptor {

    private val storedRequests = ConcurrentLinkedQueue<StoredNetworkRequest>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        val requestBodyString = requestBody?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            buffer.readUtf8()
        }

        val response = chain.proceed(request)

        val responseBodyString = response.body.string()
        val newResponse = response.newBuilder()
            .body(responseBodyString.toResponseBody(response.body?.contentType()))
            .build()

        val storedRequest = StoredNetworkRequest(
            url = request.url.toString(),
            method = request.method,
            headers = request.headers.toMap(),
            body = requestBodyString,
            timestamp = System.currentTimeMillis(),
            responseCode = response.code,
            responseBody = responseBodyString
        )

        storedRequests.offer(storedRequest)

        return newResponse
    }

    override fun clear() {
        storedRequests.clear()
    }

    override fun getStoredRequests(): List<StoredNetworkRequest> {
        return storedRequests.toList()
    }
}
