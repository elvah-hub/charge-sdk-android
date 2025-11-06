package de.elvah.charge.platform.network.retrofit.interceptor

import de.elvah.charge.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
