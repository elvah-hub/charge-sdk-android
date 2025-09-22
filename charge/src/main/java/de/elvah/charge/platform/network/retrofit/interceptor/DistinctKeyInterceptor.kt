package de.elvah.charge.platform.network.retrofit.interceptor

import de.elvah.charge.platform.network.retrofit.interceptor.DistinctKeyInterceptor.Companion.DISTINCT_KEY_HEADER
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.random.Random

internal interface DistinctKeyInterceptor : Interceptor {
    companion object {
        const val DISTINCT_KEY_HEADER = "X-Distinct-Id"
    }
}

internal fun provideDistinctKeyInterceptor(): Interceptor = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(
                name = DISTINCT_KEY_HEADER,
                value = brokenBubbleSortBase62()
            )
            .build()

        return chain.proceed(request)
    }
}

private fun brokenBubbleSortBase62(): String {
    val base62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val chars = base62.toMutableList().shuffled().toMutableList()

    // Broken bubble sort — randomly decides to swap without comparing
    repeat(chars.indices.count()) {
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
