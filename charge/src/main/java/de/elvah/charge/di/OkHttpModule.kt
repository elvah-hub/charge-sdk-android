package de.elvah.charge.di

import android.content.Context
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.network.okhttp.OkHttpFactory
import de.elvah.charge.platform.network.retrofit.ApiKeyInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

internal fun provideOkHttpClient(
    context: Context,
): OkHttpClient {
    return OkHttpFactory(
        context = context,
    ).okHttp()
}

internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor(
).apply { level = HttpLoggingInterceptor.Level.BODY }

internal fun provideApiKeyInterceptor(): ApiKeyInterceptor {
    return object : ApiKeyInterceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder().header(
                API_KEY_HEADER,
                ChargeConfig.config.apiKey
            ).build()

            return chain.proceed(request)
        }
    }
}

private const val API_KEY_HEADER = "x-api-key"

val okHttpModule = module {
    single { provideOkHttpClient(get()) }
    single { provideHttpLoggingInterceptor() }
    single { provideApiKeyInterceptor() }
}