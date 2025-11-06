package de.elvah.charge.platform.network.retrofit.interceptor

import okhttp3.logging.HttpLoggingInterceptor

internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
