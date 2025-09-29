package de.elvah.charge.platform.network.retrofit

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import de.elvah.charge.platform.network.retrofit.interceptor.ApiKeyInterceptor
import de.elvah.charge.platform.network.retrofit.interceptor.UserAgentInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


internal class RetrofitFactory(
    private val okHttpClient: OkHttpClient,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val apiKeyInterceptor: ApiKeyInterceptor,
    private val distinctKeyInterceptor: Interceptor,
    private val userAgentInterceptor: UserAgentInterceptor,
    private val chuckerInterceptor: ChuckerInterceptor,
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
        httpClientBuilder.addInterceptor(distinctKeyInterceptor)
        httpClientBuilder.addInterceptor(userAgentInterceptor)
        httpClientBuilder.addInterceptor(httpLoggingInterceptor)
        httpClientBuilder.addInterceptor(httpLoggingInterceptor)
        httpClientBuilder.addInterceptor(chuckerInterceptor)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClientBuilder.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(service)
    }
}
