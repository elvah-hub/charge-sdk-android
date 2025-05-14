package de.elvah.charge.platform.network.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

internal object RetrofitModule {


    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()


    fun provideRetrofitFactory(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor
    ): RetrofitFactory {
        return RetrofitFactory(
            okHttpClient = okHttpClient,
            httpLoggingInterceptor = httpLoggingInterceptor,
            moshi = moshi,
            apiKeyInterceptor = apiKeyInterceptor
        )
    }
}
