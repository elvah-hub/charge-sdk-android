package de.elvah.charge.platform.network.discovery

import de.elvah.charge.platform.network.annotations.NetworkInterceptor
import okhttp3.Interceptor
import kotlin.reflect.full.findAnnotation

public object NetworkInterceptorRegistry {

    private val registeredInterceptors = mutableListOf<Interceptor>()

    public fun register(interceptor: Interceptor) {
        registeredInterceptors.add(interceptor)
    }

    public fun getCustomInterceptors(): List<Interceptor> {
        return registeredInterceptors.sortedBy { interceptor ->
            interceptor::class.findAnnotation<NetworkInterceptor>()?.priority ?: 0
        }
    }

    public fun clear() {
        registeredInterceptors.clear()
    }
}
