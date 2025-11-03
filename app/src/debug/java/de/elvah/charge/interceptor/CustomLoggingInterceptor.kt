package de.elvah.charge.interceptor

import android.util.Log
import de.elvah.charge.platform.network.annotations.NetworkInterceptor
import okhttp3.Interceptor
import okhttp3.Response

@NetworkInterceptor(priority = 1)
class CustomLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        Log.d("CustomLoggingInterceptor", "→ ${request.method} ${request.url}")

        val response = chain.proceed(request)
        val endTime = System.currentTimeMillis()

        Log.d(
            "CustomLoggingInterceptor",
            "← ${response.code} ${request.url} (${endTime - startTime}ms)"
        )

        return response
    }
}
