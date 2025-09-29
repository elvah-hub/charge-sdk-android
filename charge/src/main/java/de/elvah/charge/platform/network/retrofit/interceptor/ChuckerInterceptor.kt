package de.elvah.charge.platform.network.retrofit.interceptor

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager

internal fun provideChuckerInterceptor(context: Context): ChuckerInterceptor {
    val chuckerCollector = ChuckerCollector(
        context = context,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.ONE_HOUR
    )

    return ChuckerInterceptor.Builder(context)
        .collector(chuckerCollector)
        .alwaysReadResponseBody(true)
        .createShortcut(true)
        .build()
}
