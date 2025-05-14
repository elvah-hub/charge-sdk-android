package de.elvah.charge.platform.network.retrofit

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


internal class RetrofitFactory(
    private val okHttpClient: OkHttpClient,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val apiKeyInterceptor: ApiKeyInterceptor,
    private val moshi: Moshi,
) {

    fun <T> retrofit(
        baseUrl: String,
        service: Class<T>,
    ): T {
        val httpClientBuilder = okHttpClient.newBuilder()

        // needs to be the last in line in order to show headers that have been added
        // by interceptors
        httpClientBuilder.addInterceptor(apiKeyInterceptor)
        httpClientBuilder.addInterceptor(httpLoggingInterceptor)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClientBuilder.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(service)
    }
}
