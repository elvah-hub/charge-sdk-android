package de.elvah.charge.platform.network.retrofit.interceptor

import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.network.retrofit.interceptor.ApiKeyInterceptor.Companion.API_KEY_HEADER
import okhttp3.Interceptor
import okhttp3.Response

internal interface ApiKeyInterceptor : Interceptor {
    companion object {
        const val API_KEY_HEADER = "X-Api-Key"
    }
}

internal fun provideApiKeyInterceptor(config: Config): ApiKeyInterceptor =
    object : ApiKeyInterceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder().header(
                name = API_KEY_HEADER,
                value = config.apiKey
            ).build()

            return chain.proceed(request)
        }
    }
