package de.elvah.charge.platform.network.retrofit.interceptor

import de.elvah.charge.BuildConfig
import de.elvah.charge.platform.network.retrofit.interceptor.UserAgentInterceptor.Companion.USER_AGENT_HEADER
import okhttp3.Interceptor
import okhttp3.Response

internal interface UserAgentInterceptor : Interceptor {
    companion object {
        const val USER_AGENT_HEADER = "user-agent"
    }
}

internal fun provideUserAgentInterceptor(): UserAgentInterceptor =
    object : UserAgentInterceptor {
        private val sdkVersion = BuildConfig.SDK_VERSION

        private val userAgent = "android/$sdkVersion"

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .header(USER_AGENT_HEADER, userAgent)
                .build()

            return chain.proceed(request)
        }
    }
