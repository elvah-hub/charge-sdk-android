package de.elvah.charge.platform.network.okhttp

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit


internal class OkHttpFactory(
    context: Context,
) {

    private val cache = Cache(
        File(context.cacheDir, cacheFolder),
        cacheSize,
    )

    fun okHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(
                cache
            )
            .connectTimeout(
                timeoutInSecs,
                TimeUnit.SECONDS,
            )
            .readTimeout(
                timeoutInSecs,
                TimeUnit.SECONDS,
            )
            .writeTimeout(
                timeoutInSecs,
                TimeUnit.SECONDS,
            )
            .build()
    }

    private companion object {

        // 50 MiB
        private const val cacheSize = 50L * 1024L * 1024L

        private const val cacheFolder = "http_cache"

        private const val timeoutInSecs = 20L
    }
}
