package de.elvah.charge.platform.network





internal class ApiUrlBuilder() {

    fun url(
        serviceName: String,
        baseDomain: String = "elvah.de",
    ): String {
        val envPrefix = "int"

        return "https://$serviceName.$envPrefix.$baseDomain"
    }
}
