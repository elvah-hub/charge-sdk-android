package de.elvah.charge.platform.network.retrofit.interceptor

import de.elvah.charge.BuildConfig
import de.elvah.charge.platform.network.retrofit.interceptor.ApiVersionInterceptor.Companion.API_VERSION_HEADER
import okhttp3.Interceptor
import okhttp3.Response

internal interface ApiVersionInterceptor : Interceptor {
    companion object {
        const val API_VERSION_HEADER = "X-Integrate-Version"
    }
}

internal fun provideApiVersionInterceptor(): ApiVersionInterceptor =
    object : ApiVersionInterceptor {
        private val apiVersion = BuildConfig.SDK_API_VERSION

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .header(API_VERSION_HEADER, apiVersion)
                .build()

            return chain.proceed(request)
        }
    }
