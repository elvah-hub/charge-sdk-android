package de.elvah.charge.platform.network.okhttp.di

import android.content.Context
import de.elvah.charge.platform.network.okhttp.OkHttpFactory
import de.elvah.charge.platform.network.retrofit.interceptor.provideApiKeyInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.provideDistinctKeyInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.provideHttpLoggingInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.provideUserAgentInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module

private fun provideOkHttpClient(context: Context): OkHttpClient = OkHttpFactory(
    context = context,
).okHttp()

val okHttpModule = module {
    single { provideOkHttpClient(get()) }
    single { provideHttpLoggingInterceptor() }
    single { provideApiKeyInterceptor(get()) }
    single { provideUserAgentInterceptor() }
    single { provideDistinctKeyInterceptor() }
}
