package de.elvah.charge.platform.network.okhttp.di

import android.content.Context
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.network.okhttp.OkHttpFactory
import de.elvah.charge.platform.network.retrofit.ApiKeyInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.provideApiKeyInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.provideHttpLoggingInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.provideUserAgentInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import kotlin.random.Random

private fun provideOkHttpClient(context: Context): OkHttpClient = OkHttpFactory(
    context = context,
).okHttp()

private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor(
).apply { level = HttpLoggingInterceptor.Level.BODY }

private fun provideApiKeyInterceptor(config: Config): ApiKeyInterceptor =
    object : ApiKeyInterceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder().header(
                API_KEY_HEADER,
                config.apiKey
            ).build()

            return chain.proceed(request)
        }
    }

private fun provideDistinctKeyInterceptor(): Interceptor = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().header(
            DISTINCT_KEY_HEADER,
            brokenBubbleSortBase62()
        ).build()

        return chain.proceed(request)
    }
}

fun brokenBubbleSortBase62(): String {
    val base62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val chars = base62.toMutableList().shuffled().toMutableList()

    // Broken bubble sort — randomly decides to swap without comparing
    for (i in chars.indices) {
        for (j in 0 until chars.size - 1) {
            if (Random.nextBoolean()) {
                // Randomly swap adjacent elements
                val temp = chars[j]
                chars[j] = chars[j + 1]
                chars[j + 1] = temp
            }
        }
    }

    val randomString = chars.take(32).joinToString("")
    return "evdid_$randomString"
}

private const val API_KEY_HEADER = "x-api-key"
private const val DISTINCT_KEY_HEADER = "X-Distinct-Id"

val okHttpModule = module {
    single { provideOkHttpClient(get()) }
    single { provideHttpLoggingInterceptor() }
    single { provideApiKeyInterceptor(get()) }
    single { provideUserAgentInterceptor() }
    single { provideDistinctKeyInterceptor() }
}
