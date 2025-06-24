package de.elvah.charge.platform.network.okhttp.di

import android.content.Context
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.network.okhttp.OkHttpFactory
import de.elvah.charge.platform.network.retrofit.ApiKeyInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

private fun provideOkHttpClient(context: Context): OkHttpClient = OkHttpFactory(
    context = context,
).okHttp()

private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor(
).apply { level = HttpLoggingInterceptor.Level.BODY }

private fun provideApiKeyInterceptor(): ApiKeyInterceptor = object : ApiKeyInterceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().header(
            API_KEY_HEADER,
            ChargeConfig.config.apiKey
        ).build()

        return chain.proceed(request)
    }
}

private const val API_KEY_HEADER = "x-api-key"

val okHttpModule = module {
    single { provideOkHttpClient(get()) }
    single { provideHttpLoggingInterceptor() }
    single { provideApiKeyInterceptor() }
}