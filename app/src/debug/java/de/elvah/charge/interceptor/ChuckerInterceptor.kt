package de.elvah.charge.interceptor

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager

fun getChuckerInterceptor(context: Context): ChuckerInterceptor {
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
