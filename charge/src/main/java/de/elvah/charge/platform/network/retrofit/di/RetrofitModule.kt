package de.elvah.charge.platform.network.retrofit.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.elvah.charge.platform.network.retrofit.RetrofitFactory
import de.elvah.charge.platform.network.retrofit.interceptor.ApiKeyInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.UserAgentInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module

private fun provideMoshi(): Moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private fun provideRetrofitFactory(
    okHttpClient: OkHttpClient,
    moshi: Moshi,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    apiKeyInterceptor: ApiKeyInterceptor,
    userAgentInterceptor: UserAgentInterceptor,
    distinctKeyInterceptor: Interceptor,
    chuckerInterceptor: ChuckerInterceptor,
): RetrofitFactory {
    return RetrofitFactory(
        okHttpClient = okHttpClient,
        httpLoggingInterceptor = httpLoggingInterceptor,
        moshi = moshi,
        apiKeyInterceptor = apiKeyInterceptor,
        distinctKeyInterceptor = distinctKeyInterceptor,
        userAgentInterceptor = userAgentInterceptor,
        chuckerInterceptor = chuckerInterceptor,
    )
}

internal val retrofitModule = module {
    single { provideMoshi() }
    single { provideRetrofitFactory(get(), get(), get(), get(), get(), get(), get()) }
}
