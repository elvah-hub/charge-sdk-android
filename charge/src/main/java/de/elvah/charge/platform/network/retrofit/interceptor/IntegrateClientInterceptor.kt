package de.elvah.charge.platform.network.retrofit.interceptor

import de.elvah.charge.BuildConfig
import de.elvah.charge.platform.network.retrofit.interceptor.IntegrateClientInterceptor.Companion.PLATFORM
import de.elvah.charge.platform.network.retrofit.interceptor.IntegrateClientInterceptor.Companion.USER_AGENT_HEADER
import okhttp3.Interceptor
import okhttp3.Response

internal interface IntegrateClientInterceptor : Interceptor {
    companion object {
        const val USER_AGENT_HEADER = "X-Integrate-Client"
        const val PLATFORM = "android"
    }
}

internal fun provideIntegrateClientInterceptor(): IntegrateClientInterceptor =
    object : IntegrateClientInterceptor {
        private val sdkVersion = BuildConfig.SDK_VERSION

        private val userAgent = "$PLATFORM/$sdkVersion"

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .header(USER_AGENT_HEADER, userAgent)
                .build()

            return chain.proceed(request)
        }
    }
