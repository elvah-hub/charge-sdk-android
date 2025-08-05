package de.elvah.charge.platform.network.retrofit.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.elvah.charge.platform.network.retrofit.ApiKeyInterceptor
import de.elvah.charge.platform.network.retrofit.RetrofitFactory
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
    distinctKeyInterceptor: okhttp3.Interceptor
): RetrofitFactory {
    return RetrofitFactory(
        okHttpClient = okHttpClient,
        httpLoggingInterceptor = httpLoggingInterceptor,
        moshi = moshi,
        apiKeyInterceptor = apiKeyInterceptor,
        distinctKeyInterceptor = distinctKeyInterceptor
    )
}

val retrofitModule = module {
    single { provideMoshi() }
    single { provideRetrofitFactory(get(), get(), get(), get(), get()) }
}
