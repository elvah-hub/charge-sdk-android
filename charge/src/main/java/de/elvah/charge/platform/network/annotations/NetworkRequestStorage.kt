package de.elvah.charge.platform.network.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NetworkRequestStorage(
    val priority: Int = 0
)