package de.elvah.charge.platform.network.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class NetworkInterceptor(
    val priority: Int = 0
)
